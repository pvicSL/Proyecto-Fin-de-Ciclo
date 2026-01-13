import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';

// Estados posibles para el recuadro de modificación o cancelación de una cita
type ModifierState = 'search' | 'inicial' | 'modificando' | 'confirmado' | 'cancelado';

interface DayColumn {
  dateObj: Date;
  dateStr: string;
  weekday: string;
  dayNumber: string;
  slots: string[];
  // Añadimos tipado estricto para controlar las clases CSS en el HTML
  status: 'available' | 'weekend' | 'disabled' | 'empty';
}

@Component({
  selector: 'app-appointment-modifier',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './appointment-modifier.html', // Verifica extensión .html
  styleUrl: './appointment-modifier.css'     // Verifica extensión .css o .scss
})
export class AppointmentModifierComponent {

  @Output() closeRequest = new EventEmitter<void>();

  currentState: ModifierState = 'search';

  // VARIABLES DE BÚSQUEDA
  bookingCode: string = '';  // Referencia
  bookingEmail: string = ''; // Email

  currentAppointment: AppointmentDTO | null = null;
  calculatedDuration: number = 0;

  selectedNewDate: string = '';
  availableSlots: string[] = [];

  viewDetails = {
    weekday: '',
    day: '',
    year: '',
    hour: '',
    durationText: ''
  };

  // VARIABLES PARA EL CALENDARIO SEMANAL
  allDays: DayColumn[] = [];
  weekIndex: number = 0;
  daysPerPage: number = 7;

  constructor(private appointmentService: AppointmentService) { }

  // --- GETTER PARA EL ENCABEZADO (NUEVO: REQUISITO DE FORMATO SEMANA) ---
  get currentWeekHeader(): string {
    if (this.visibleDays.length === 0) return '';

    // Cogemos el primer día visible (que siempre será lunes gracias a la nueva lógica)
    const firstDay = this.visibleDays[0].dateObj;

    // Nombre del mes
    const monthName = new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' })
      .format(firstDay).toUpperCase();

    // Número de semana del año
    const weekNumber = this.getWeekNumber(firstDay);

    return `${monthName} - SEMANA ${weekNumber}`;
  }

  // --- MÉTODOS DE LÓGICA DE NEGOCIO ---

  formatSlotDate(isoDate: string): string {
    const safeDate = isoDate.replace(' ', 'T');
    const date = new Date(safeDate);
    if (isNaN(date.getTime())) return isoDate;

    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });
    const timeFormatter = new Intl.DateTimeFormat('es-ES', { hour: '2-digit', minute: '2-digit' });

    return `${dayFormatter.format(date).toUpperCase()} - ${timeFormatter.format(date)}`;
  }

  searchAppointment() {
    if (!this.bookingCode.trim() || !this.bookingEmail.trim()) return;

    this.appointmentService.getAppointmentByLocator(this.bookingCode, this.bookingEmail.toLowerCase()).subscribe({
      next: (data) => {
        this.currentAppointment = data;

        // Comprobamos si la cita ya está cancelada o rechazada
        // (Asegúrate que tu DTO trae el estatus como string)
        if (data.estatus === 'RECHAZADO' || data.estatus === 'CANCELADO') {
          alert('Esta cita ya figura como cancelada.');
          this.changeState('cancelado');
          return;
        }

        // USAMOS EL DATO DEL BACKEND (duracionEstimada)
        // IMPORTANTE: Asegúrate de que AppointmentDTO tiene este campo opcional
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

  // --- MODIFICADO: PREPARADO PARA FILTROS ---
  startModification() {
    if (!this.currentAppointment) return;

    console.log("Buscando huecos para:", this.calculatedDuration, "minutos");

    /* -----------------------------------------------------------------------
       PREPARACIÓN PARA FUTURO FILTRADO POR EMPLEADO
       Extraemos el tipo y estilo de la cita actual para buscar huecos compatibles.
       ----------------------------------------------------------------------- */
    const filtrosFuturos = {
      tipo: this.currentAppointment.tipo,   // Asegúrate de que el DTO tenga estos campos
      estilo: this.currentAppointment.estilo
    };

    // Llamamos al Back. Dejamos 'filtrosFuturos' comentado en la llamada 
    // para que no dé error hasta que el service/backend estén listos.
    this.appointmentService.getAvailableSlots(this.calculatedDuration /*, filtrosFuturos */).subscribe({
      next: (dataBackend) => {
        // CAMBIO: Usamos la nueva lógica 'generateCalendarGrid' en vez de 'transformarMapaADias'
        // para garantizar semanas completas de Lunes a Domingo.
        this.allDays = this.generateCalendarGrid(dataBackend);
        this.weekIndex = 0;
        this.selectedNewDate = '';
        this.changeState('modificando');
      },
      error: (err) => alert("Error cargando disponibilidad")
    });
  }

  // --- MÉTODOS DE NAVEGACIÓN Y VISUALIZACIÓN ---

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

  // --- NUEVA LÓGICA DE CALENDARIO (REQUISITO: LUNES A DOMINGO) ---
  // Sustituye a 'transformarMapaADias' para rellenar huecos vacíos y forzar inicio en lunes.
  private generateCalendarGrid(dataBackend: any): DayColumn[] {
    const days: DayColumn[] = [];
    const formatterDia = new Intl.DateTimeFormat('es-ES', { weekday: 'short' });

    // 1. Calcular fecha de inicio (Mínimo: hoy + 3 días)
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3);

    // 2. Retroceder al Lunes de esa fecha mínima para que la semana empiece bien visualmente
    const startGridDate = this.getMonday(minDate);

    // 3. Determinar cuántos días cargar (ej. 5 semanas = 35 días)
    const daysToGenerate = 35;

    for (let i = 0; i < daysToGenerate; i++) {
      const iterDate = new Date(startGridDate);
      iterDate.setDate(startGridDate.getDate() + i);

      // Formato YYYY-MM-DD para buscar en el mapa del backend
      const isoDate = iterDate.toISOString().split('T')[0];
      const isWeekend = iterDate.getDay() === 0 || iterDate.getDay() === 6;
      const isLocked = iterDate < minDate; // Días pasados o margen de 3 días

      // Recuperar slots si existen en el backend para esa fecha
      const slots = dataBackend[isoDate] || [];
      const hasSlots = slots.length > 0;

      // Determinamos el estado para el CSS
      let status: 'available' | 'weekend' | 'disabled' | 'empty' = 'empty';

      if (isLocked) status = 'disabled';
      else if (isWeekend) status = 'weekend';
      else if (hasSlots) status = 'available';
      else status = 'empty'; // Día laborable sin huecos o no cargado en back

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

  // --- MODIFICADO: USA REFERENCIA Y EMAIL ---
  confirmChange() {
    if (!this.selectedNewDate || !this.currentAppointment) return;

    const dateObj = new Date(this.selectedNewDate);
    const newFecha = dateObj.toISOString().split('T')[0];
    const newHora = dateObj.toTimeString().split(' ')[0];

    // CAMBIO CRÍTICO: Usamos bookingCode (Referencia) y bookingEmail
    this.appointmentService.updateAppointmentDate(
      this.bookingCode,  // Referencia
      this.bookingEmail, // Email
      newFecha,
      newHora
    ).subscribe({
      next: (resp) => {
        // Al confirmar, volvemos a formatear para mostrar duración y datos en el resumen final
        this.formatViewDetails(newFecha, newHora);
        this.changeState('confirmado');
      },
      error: (err) => {
        console.error(err);
        alert('Error al modificar la cita. Verifica que los datos sean correctos.');
      }
    });
  }

  // --- MODIFICADO: USA REFERENCIA Y EMAIL ---
  cancelAppointment() {
    if (!this.currentAppointment) return;

    if (confirm('¿Seguro que deseas cancelar? Esta acción es irreversible.')) {

      // CAMBIO CRÍTICO: Usamos bookingCode (Referencia) y bookingEmail
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

  // --- UTILIDADES ---

  private formatViewDetails(fecha: string | null, hora: string | null) {
    // 1. Protección contra nulos (por si el backend falla)
    if (!fecha || !hora) {
      console.warn('La cita recuperada está vacía o incompleta.');
      this.viewDetails = {
        weekday: '---',
        day: '--',
        year: '----',
        hour: '--:--',
        durationText: ''
      };
      return;
    }

    // 2. Construcción segura de la fecha
    const fechaLimpia = fecha.toString().split('T')[0];
    const fullDate = new Date(`${fechaLimpia}T${hora}`);

    if (isNaN(fullDate.getTime())) {
      console.error('Fecha inválida tras concatenar:', fecha, hora);
      return;
    }

    const weekdayFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' });
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });

    // CÁLCULO DE DURACIÓN (Nuevo requisito: mostrar duración al confirmar también)
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

  // Utilidad para obtener el lunes de una fecha dada
  private getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // Ajuste: Lunes=1 ... Domingo=0(-6)
    return new Date(date.setDate(diff));
  }

  // Utilidad para obtener número de semana (Estándar ISO 8601)
  private getWeekNumber(d: Date): number {
    const date = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
    const dayNum = date.getUTCDay() || 7;
    date.setUTCDate(date.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(date.getUTCFullYear(), 0, 1));
    return Math.ceil((((date.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
  }

  closeModifier() {
    this.bookingCode = '';
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