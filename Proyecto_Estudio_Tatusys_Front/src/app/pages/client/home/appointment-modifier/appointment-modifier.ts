import { Component, Output, EventEmitter, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';

type ModifierState = 'search' | 'inicial' | 'modificando' | 'confirmado' | 'cancelado';

interface DayColumn {
  dateObj: Date;
  dateStr: string;
  weekday: string;
  dayNumber: string;
  slots: string[];
  status: 'available' | 'weekend' | 'disabled' | 'empty';
}

@Component({
  selector: 'app-appointment-modifier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appointment-modifier.html',
  styleUrl: './appointment-modifier.css'
})
export class AppointmentModifierComponent implements OnInit, OnDestroy {

  @Output() closeRequest = new EventEmitter<void>();

  currentState: ModifierState = 'search';

  // Datos búsqueda
  bookingCode: string = '';
  bookingEmail: string = '';

  // Datos cita
  currentAppointment: AppointmentDTO | null = null;
  calculatedDuration: number = 60;

  // Selección
  selectedNewDate: string = '';

  // Calendario
  allDays: DayColumn[] = [];
  weekIndex: number = 0;

  // RESPONSIVE LOGIC
  isMobile: boolean = false;
  daysPerPage: number = 7; // Se recalcula dinámicamente

  viewDetails: any = {
    weekday: '',
    day: '',
    year: '',
    hour: '',
    durationText: ''
  };

  // Listener para el resize
  private resizeListener: any;

  constructor(private appointmentService: AppointmentService) { }

  ngOnInit() {
    this.checkScreenSize();
    // Escuchamos cambios de tamaño de pantalla para adaptar el calendario en tiempo real
    this.resizeListener = () => this.checkScreenSize();
    window.addEventListener('resize', this.resizeListener);
  }

  ngOnDestroy() {
    // Limpiamos el evento al destruir el componente para evitar fugas de memoria
    if (this.resizeListener) {
      window.removeEventListener('resize', this.resizeListener);
    }
  }

  // --- LÓGICA RESPONSIVE ---
  private checkScreenSize() {
    const wasMobile = this.isMobile;
    this.isMobile = window.innerWidth <= 480;

    // Configuración según dispositivo
    if (this.isMobile) {
      this.daysPerPage = 2; // En móvil mostramos 2 días
    } else {
      this.daysPerPage = 7; // En escritorio mostramos la semana entera
    }

    // Si cambiamos de dispositivo, reseteamos la paginación para no quedarnos en una página vacía
    if (wasMobile !== this.isMobile) {
      this.weekIndex = 0;
    }
  }

  // --- GETTER INTELIGENTE: FUENTE DE DÍAS ---
  // En móvil filtra los fines de semana. En escritorio los muestra.
  get daysSource(): DayColumn[] {
    if (this.isMobile) {
      // Filtramos para quitar los fines de semana
      return this.allDays.filter(day => day.status !== 'weekend');
    }
    return this.allDays;
  }

  // --- GETTER: DÍAS VISIBLES ---
  get visibleDays(): DayColumn[] {
    const start = this.weekIndex * this.daysPerPage;
    // Usamos daysSource en lugar de allDays
    return this.daysSource.slice(start, start + this.daysPerPage);
  }

  // --- GETTER: TÍTULO DEL CALENDARIO ---
  get currentWeekHeader(): string {
    if (this.visibleDays.length === 0) return '';
    const firstDay = this.visibleDays[0].dateObj;

    const monthName = new Intl.DateTimeFormat('es-ES', { month: 'long' }).format(firstDay).toUpperCase();

    // Si estamos en móvil, mostramos "Días X - Y" para que se entienda mejor
    if (this.isMobile) {
      const lastDay = this.visibleDays[this.visibleDays.length - 1].dateObj;
      return `${monthName} (${firstDay.getDate()} - ${lastDay.getDate()})`;
    }

    const year = firstDay.getFullYear();
    return `${monthName} ${year}`;
  }

  // --- FUNCIONES DE NAVEGACIÓN ---
  nextWeek() {
    // Comprobamos contra daysSource.length
    if ((this.weekIndex + 1) * this.daysPerPage < this.daysSource.length) {
      this.weekIndex++;
    }
  }

  prevWeek() {
    if (this.weekIndex > 0) {
      this.weekIndex--;
    }
  }

  // ============================================================
  // LÓGICA DE NEGOCIO (Igual que antes)
  // ============================================================

  searchAppointment() {
    if (!this.bookingCode.trim() || !this.bookingEmail.trim()) return;

    this.appointmentService.getAppointmentByLocator(this.bookingCode, this.bookingEmail.toLowerCase())
      .subscribe({
        next: (data) => {
          this.currentAppointment = data;
          if (data.estatus === 'RECHAZADO' || data.estatus === 'CANCELADO') {
            alert('Esta cita ya figura como cancelada o rechazada.');
            this.changeState('cancelado');
            return;
          }
          this.calculatedDuration = data.duracionEstimada || 60;
          this.formatViewDetails(data.fecha, data.hora);
          this.changeState('inicial');
        },
        error: (err) => {
          console.error(err);
          alert('No se ha encontrado ninguna cita. Verifica el localizador y el email.');
        }
      });
  }

  startModification() {
    if (!this.currentAppointment) return;
    const workerId = this.currentAppointment.idTrabajador;

    if (!workerId) {
      alert("Error: Cita sin tatuador asignado. Contacta con el estudio.");
      return;
    }

    this.appointmentService.getAvailableSlots(this.calculatedDuration, workerId).subscribe({
      next: (dataBackend) => {
        this.allDays = this.generateCalendarGrid(dataBackend);
        this.weekIndex = 0;
        this.selectedNewDate = '';
        this.changeState('modificando');
      },
      error: (err) => {
        console.error(err);
        alert("Error cargando disponibilidad. Inténtalo más tarde.");
      }
    });
  }

  private generateCalendarGrid(dataBackend: any): DayColumn[] {
    const days: DayColumn[] = [];
    const formatterDia = new Intl.DateTimeFormat('es-ES', { weekday: 'short' });

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3);

    const startGridDate = this.getMonday(minDate);
    const daysToGenerate = 42; // Generamos 6 semanas para tener margen si ocultamos fines de semana

    for (let i = 0; i < daysToGenerate; i++) {
      const iterDate = new Date(startGridDate);
      iterDate.setDate(startGridDate.getDate() + i);

      const isoDate = iterDate.toISOString().split('T')[0];
      const isWeekend = (iterDate.getDay() === 0 || iterDate.getDay() === 6);
      const isLocked = iterDate < minDate;

      const slots = dataBackend[isoDate] || [];
      const hasSlots = slots.length > 0;

      let status: DayColumn['status'] = 'empty';
      if (isLocked) status = 'disabled';
      else if (isWeekend) status = 'weekend';
      else if (hasSlots) status = 'available';

      days.push({
        dateObj: iterDate,
        dateStr: isoDate,
        weekday: formatterDia.format(iterDate).toUpperCase().replace('.', ''),
        dayNumber: iterDate.getDate().toString(),
        slots: slots,
        status: status
      });
    }
    return days;
  }

  selectSlot(day: DayColumn, slotTime: string) {
    this.selectedNewDate = `${day.dateStr} ${slotTime}`;
  }

  isSlotSelected(day: DayColumn, slotTime: string): boolean {
    return this.selectedNewDate === `${day.dateStr} ${slotTime}`;
  }

  confirmChange() {
    if (!this.selectedNewDate || !this.currentAppointment) return;
    const [fecha, hora] = this.selectedNewDate.split(' ');
    const horaFull = hora + ':00';

    this.appointmentService.updateAppointmentDate(
      this.bookingCode,
      this.bookingEmail,
      fecha,
      horaFull
    ).subscribe({
      next: (resp) => {
        this.formatViewDetails(fecha, horaFull);
        this.changeState('confirmado');
      },
      error: (err) => {
        console.error(err);
        alert('Error al guardar la nueva fecha.');
      }
    });
  }

  cancelAppointment() {
    if (!this.currentAppointment) return;
    if (confirm('¿Estás seguro? La cita se cancelará definitivamente.')) {
      this.appointmentService.cancelAppointment(this.bookingCode, this.bookingEmail)
        .subscribe({
          next: () => this.changeState('cancelado'),
          error: () => alert('Error al cancelar la cita.')
        });
    }
  }

  // Formatea la info para la tarjeta resumen
  private formatViewDetails(fecha: string | null, hora: string | null) {
    if (!fecha || !hora) return;

    const fechaLimpia = fecha.toString().split('T')[0];
    const fullDate = new Date(`${fechaLimpia}T${hora}`);

    if (isNaN(fullDate.getTime())) return;

    const weekdayFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' });

    // CORRECCIÓN: Quitamos 'day: numeric' para que no repita el número.
    // Ahora solo mostrará Mes y Año (ej: "marzo de 2026")
    const monthFormatter = new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' });

    // Calculamos texto de duración
    const h = Math.floor(this.calculatedDuration / 60);
    const m = this.calculatedDuration % 60;
    let durText = '';
    if (h > 0) durText += `${h} H `;
    if (m > 0) durText += `${m} MIN`;

    this.viewDetails = {
      weekday: weekdayFormatter.format(fullDate),       // "lunes"
      day: fullDate.getDate().toString(),               // "17" (El número grande)
      year: monthFormatter.format(fullDate).toUpperCase(), // "MARZO DE 2026" (Sin el 17)
      hour: hora.substring(0, 5),                       // "10:00"
      durationText: durText.trim() ? `(${durText.trim()})` : ''
    };
  }

  private getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  }

  changeState(newState: ModifierState) {
    this.currentState = newState;
    setTimeout(() => {
      document.getElementById('modifier-card')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 100);
  }

  closeModifier() {
    this.bookingCode = '';
    this.bookingEmail = '';
    this.selectedNewDate = '';
    this.currentAppointment = null;
    this.currentState = 'search';
    this.closeRequest.emit();
  }
}