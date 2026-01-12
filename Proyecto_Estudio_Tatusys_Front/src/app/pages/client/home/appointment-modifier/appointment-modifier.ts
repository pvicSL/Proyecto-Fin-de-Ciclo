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

  // --- MÉTODOS DE CAMBIO DE ESTADO ---

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
        if (data.estatus === 'RECHAZADO' || data.estatus === 'CANCELADO') {
          alert('Esta cita ya figura como cancelada.');
          this.changeState('cancelado');
          return;
        }

        // USAMOS EL DATO DEL BACKEND (duracionEstimada)
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
        this.allDays = this.transformarMapaADias(dataBackend);
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

  // --- FUNCIÓN AUXILIAR DE TRANSFORMACIÓN ---
  private transformarMapaADias(dataBackend: any): DayColumn[] {
    const diasTemp: DayColumn[] = [];
    const formatterDia = new Intl.DateTimeFormat('es-ES', { weekday: 'short' });

    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3);

    for (const fechaStr in dataBackend) {
      if (dataBackend.hasOwnProperty(fechaStr)) {
        const fechaObj = new Date(fechaStr);
        const fechaObjMidnight = new Date(fechaObj);
        fechaObjMidnight.setHours(0, 0, 0, 0);

        if (fechaObjMidnight >= minDate) {
          diasTemp.push({
            dateObj: fechaObj,
            dateStr: fechaStr,
            weekday: formatterDia.format(fechaObj).toUpperCase().replace('.', ''),
            dayNumber: fechaObj.getDate().toString(),
            slots: dataBackend[fechaStr]
          });
        }
      }
    }

    return diasTemp.sort((a, b) => a.dateObj.getTime() - b.dateObj.getTime());
  }

  // --- MODIFICADO: USA REFERENCIA Y EMAIL ---
  confirmChange() {
    if (!this.selectedNewDate || !this.currentAppointment) return;

    const dateObj = new Date(this.selectedNewDate);
    const newFecha = dateObj.toISOString().split('T')[0];
    const newHora = dateObj.toTimeString().split(' ')[0];

    this.appointmentService.updateAppointmentDate(
      this.bookingCode,  // Referencia
      this.bookingEmail, // Email
      newFecha,
      newHora
    ).subscribe({
      next: (resp) => {
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


  private formatViewDetails(fecha: string | null, hora: string | null) {
    // 1. Protección contra nulos
    if (!fecha || !hora) {
      console.warn('La cita recuperada está vacía o incompleta.');
      // Dejamos los textos vacíos para que no se rompa la UI
      this.viewDetails = {
        weekday: '---',
        day: '--',
        year: '----',
        hour: '--:--',
        durationText: ''
      };
      return; // IMPORTANTE: Salimos aquí para no ejecutar el new Date()
    }

    // 2. Si hay datos, seguimos con la lógica normal
    const fechaLimpia = fecha.toString().split('T')[0];
    const fullDate = new Date(`${fechaLimpia}T${hora}`);

    if (isNaN(fullDate.getTime())) {
      console.error('Fecha inválida tras concatenar:', fecha, hora);
      return;
    }

    const weekdayFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' });
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });

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

  closeModifier() {
    // Reseteamos las variables
    this.bookingCode = '';
    this.selectedNewDate = '';
    this.currentAppointment = null;
    this.currentState = 'search';

    // Emitimos el evento para avisar al padre que cierre el modal
    this.closeRequest.emit();
  }

  changeState(newState: ModifierState) {
    this.currentState = newState;
    setTimeout(() => {
      document.getElementById('modifier-card')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 100);
  }

}