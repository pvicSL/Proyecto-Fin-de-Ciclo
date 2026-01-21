import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';

// Estados posibles para el recuadro de modificación
type ModifierState = 'search' | 'inicial' | 'modificando' | 'confirmado' | 'cancelado';

// Estados de los días del calendario
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
export class AppointmentModifierComponent {

  @Output() closeRequest = new EventEmitter<void>();

  /* ============================================================
   ===== BÚSQUEDA DE CITA EXISTENTE, CON MAIL Y REFERENCIA ===== 
   ==============================================================*/
  currentState: ModifierState = 'search';

  // Variables de la búsqueda
  bookingCode: string = '';  // Referencia de la cita (Ej: A1B2C3D4)
  bookingEmail: string = ''; // Email del cliente

  currentAppointment: AppointmentDTO | null = null;
  calculatedDuration: number = 0;

  // Variables para reasignar una fecha
  selectedNewDate: string = '';
  availableSlots: string[] = [];

  // Variables para el calendario semanal
  allDays: DayColumn[] = [];
  weekIndex: number = 0;
  daysPerPage: number = 7;

  // Datos del resumen visual (Ej: tu cita es el XXX a las XXX)
  viewDetails = {
    weekday: '',
    day: '',
    year: '',
    hour: '',
    durationText: ''
  };

  constructor(private appointmentService: AppointmentService) { }

  // --- GETTER PARA EL ENCABEZADO (MES Y SEMANA) ---
  get currentWeekHeader(): string {
    if (this.visibleDays.length === 0) return '';

    const firstDay = this.visibleDays[0].dateObj;
    const monthName = new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' })
      .format(firstDay).toUpperCase();
    const weekNumber = this.getWeekNumber(firstDay);

    return `${monthName} - SEMANA ${weekNumber}`;
  }

  // --- LÓGICA DE BÚSQUEDA ---
  searchAppointment() {
    if (!this.bookingCode.trim() || !this.bookingEmail.trim()) return;

    this.appointmentService.getAppointmentByLocator(this.bookingCode, this.bookingEmail.toLowerCase()).subscribe({
      next: (data) => {
        this.currentAppointment = data;

        // Validar si ya está cancelada, por si acaso (podría haberla cancelado el estudio, por ejemplo)
        if (data.estatus === 'RECHAZADO' || data.estatus === 'CANCELADO') {
          alert('Esta cita ya figura como cancelada.');
          this.changeState('cancelado');
          return;
        }

        // Recuperar la duración guardada en BBDD
        this.calculatedDuration = data.duracionEstimada || 60;

        this.formatViewDetails(data.fecha, data.hora);
        this.changeState('inicial');
      },
      error: (err) => {
        console.error(err);
        alert('No se ha encontrado ninguna cita con esos datos. Verifica el localizador y el email.');
      }
    });
  }

  // --- LÓGICA DE MODIFICACIÓN (CALENDARIO) ---
  startModification() {
    if (!this.currentAppointment) return;

    // Recuperamos el ID del trabajador (asegurándonos de que existe)
    const workerId = this.currentAppointment.idTrabajador;

    // PROTECCIÓN: Si es una cita antigua sin trabajador asignado
    if (!workerId) {
      alert("Esta cita es antigua y no tiene un tatuador asignado. Por favor, contacta con el estudio para modificarla manualmente.");
      return;
    }

    console.log(`Buscando huecos para trabajador ${workerId} con duración ${this.calculatedDuration} min`);

    // Llamamos al servicio (ahora seguro que workerId tiene valor)
    this.appointmentService.getAvailableSlots(this.calculatedDuration, workerId).subscribe({
      next: (dataBackend) => {
        this.allDays = this.generateCalendarGrid(dataBackend);
        this.weekIndex = 0;
        this.selectedNewDate = '';
        this.changeState('modificando');
      },
      error: (err) => {
        console.error(err);
        alert("Error cargando la disponibilidad del trabajador.");
      }
    });
  }

  // --- LÓGICA DE CALENDARIO (GENERACIÓN Y NAVEGACIÓN) ---

  private generateCalendarGrid(dataBackend: any): DayColumn[] {
    const days: DayColumn[] = [];
    const formatterDia = new Intl.DateTimeFormat('es-ES', { weekday: 'short' });

    // Fecha de inicio: hoy + 3 días de margen
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3);

    // La semana siempre empieza visualmente por el lunes
    const startGridDate = this.getMonday(minDate);

    // GENERA 35 días, quizá se debería cambiar a un margen más amplio
    const daysToGenerate = 35;

    for (let i = 0; i < daysToGenerate; i++) {
      const iterDate = new Date(startGridDate);
      iterDate.setDate(startGridDate.getDate() + i);

      const isoDate = iterDate.toISOString().split('T')[0];
      const isWeekend = iterDate.getDay() === 0 || iterDate.getDay() === 6;
      const isLocked = iterDate < minDate;

      const slots = dataBackend[isoDate] || [];
      const hasSlots = slots.length > 0;

      let status: 'available' | 'weekend' | 'disabled' | 'empty' = 'empty';

      if (isLocked) status = 'disabled';
      else if (isWeekend) status = 'weekend';
      else if (hasSlots) status = 'available';
      else status = 'empty';

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

  get visibleDays(): DayColumn[] {
    const start = this.weekIndex * this.daysPerPage;
    return this.allDays.slice(start, start + this.daysPerPage);
  }

  nextWeek() {
    if ((this.weekIndex + 1) * this.daysPerPage < this.allDays.length) {
      this.weekIndex++;
    }
  }

  prevWeek() {
    if (this.weekIndex > 0) {
      this.weekIndex--;
    }
  }

  selectSlot(day: DayColumn, slotTime: string) {
    this.selectedNewDate = `${day.dateStr} ${slotTime}`;
  }

  isSlotSelected(day: DayColumn, slotTime: string): boolean {
    return this.selectedNewDate === `${day.dateStr} ${slotTime}`;
  }

  // --- ACCIONES FINALES (CONFIRMAR / CANCELAR) ---
  confirmChange() {
    if (!this.selectedNewDate || !this.currentAppointment) return;

    const dateObj = new Date(this.selectedNewDate);
    const newFecha = dateObj.toISOString().split('T')[0];
    const newHora = dateObj.toTimeString().split(' ')[0];

    this.appointmentService.updateAppointmentDate(
      this.bookingCode,
      this.bookingEmail,
      newFecha,
      newHora
    ).subscribe({
      next: (resp) => {
        this.formatViewDetails(newFecha, newHora);
        this.changeState('confirmado');
        console.log("Se ha guardado el cambio correctamente.")
      },
      error: (err) => {
        console.error(err);
        alert('Error al modificar la cita. Inténtalo de nuevo.');
      }
    });
  }

  cancelAppointment() {
    if (!this.currentAppointment) return;

    if (confirm('¿Seguro que deseas cancelar? Esta acción es irreversible y perderás la fianza.')) {
      this.appointmentService.cancelAppointment(
        this.bookingCode,
        this.bookingEmail
      ).subscribe({
        next: () => this.changeState('cancelado'),
        error: (err) => {
          console.error(err);
          alert('Error al cancelar la cita.');
        }
      });
    }
  }

  // --- UTILIDADES Y FORMATO ---
  formatSlotDate(isoDate: string): string {
    const safeDate = isoDate.replace(' ', 'T');
    const date = new Date(safeDate);
    if (isNaN(date.getTime())) return isoDate;

    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });
    const timeFormatter = new Intl.DateTimeFormat('es-ES', { hour: '2-digit', minute: '2-digit' });

    return `${dayFormatter.format(date).toUpperCase()} - ${timeFormatter.format(date)}`;
  }

  private formatViewDetails(fecha: string | null, hora: string | null) {
    if (!fecha || !hora) {
      this.viewDetails = { weekday: '---', day: '--', year: '----', hour: '--:--', durationText: '' };
      return;
    }

    const fechaLimpia = fecha.toString().split('T')[0];
    const fullDate = new Date(`${fechaLimpia}T${hora}`);

    if (isNaN(fullDate.getTime())) return;

    const weekdayFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' });
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });

    // Cálculo de texto de duración (ej: 2 horas, para que el cliente sepa lo que dura su cita)
    const horas = Math.floor(this.calculatedDuration / 60);
    const minRestantes = this.calculatedDuration % 60;
    let durText = '';
    if (horas > 0) durText += `${horas} h `;
    if (minRestantes > 0) durText += `${minRestantes} min`;

    this.viewDetails = {
      weekday: weekdayFormatter.format(fullDate),
      day: dayFormatter.format(fullDate).toUpperCase(),
      year: fullDate.getFullYear().toString(),
      hour: hora.substring(0, 5),
      durationText: `(${durText.trim()})`
    };
  }

  private getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  }

  private getWeekNumber(d: Date): number {
    const date = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
    const dayNum = date.getUTCDay() || 7;
    date.setUTCDate(date.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(date.getUTCFullYear(), 0, 1));
    return Math.ceil((((date.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
  }

  closeModifier() {
    this.bookingCode = '';
    this.bookingEmail = '';
    this.selectedNewDate = '';
    this.currentAppointment = null;
    this.currentState = 'search';
    this.closeRequest.emit();
  }

  changeState(newState: ModifierState) {
    this.currentState = newState;
    setTimeout(() => {
      document.getElementById('modifier-card')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 100);
  }
}