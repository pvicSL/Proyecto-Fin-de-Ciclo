import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';

type ModifierState = 'search' | 'inicial' | 'modificando' | 'confirmado' | 'cancelado';

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
  bookingCode: string = ''; // El ID introducido por el usuario

  currentAppointment: AppointmentDTO | null = null;
  calculatedDuration: number = 0; // Duración en minutos calculada

  selectedNewDate: string = '';
  availableSlots: string[] = [];

  viewDetails = {
    weekday: '',
    day: '',
    year: '',
    hour: '',
    durationText: '' // Para mostrar "(2 horas)"
  };

  constructor(private appointmentService: AppointmentService) { }

  // --- MÉTODOS DE CAMBIO DE ESTADO ---
  // Formatea la fecha ISO (2026-12-20T10:00:00) para que se vea bonita en el Select
  formatSlotDate(isoDate: string): string {
    const date = new Date(isoDate);
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });
    const timeFormatter = new Intl.DateTimeFormat('es-ES', { hour: '2-digit', minute: '2-digit' });

    // Devuelve algo como: "20 DIC - 10:00"
    return `${dayFormatter.format(date).toUpperCase()} - ${timeFormatter.format(date)}`;
  }

  searchAppointment() {
    if (!this.bookingCode.trim()) return;

    this.appointmentService.getAppointmentByRef(this.bookingCode).subscribe({
      next: (data) => {
        this.currentAppointment = data;

        // Validamos si ya está cancelada
        if (data.estatus === 'RECHAZADO' || data.estatus === 'CANCELADO') {
          alert('Esta cita ya figura como cancelada.');
          this.changeState('cancelado');
          return;
        }

        // 1. Calculamos la duración basada en el TAMAÑO del DTO
        this.calculatedDuration = this.calculateDuration(data.tamanio);

        // 2. Formateamos textos para la vista
        this.formatViewDetails(data.fecha, data.hora);

        this.changeState('inicial');
      },
      error: (err) => {
        console.error(err);
        alert('No se ha encontrado ninguna cita con esa referencia.');
      }
    });
  }

  startModification() {
    // Pedimos huecos libres basándonos en la duración calculada
    this.appointmentService.getAvailableSlots(this.calculatedDuration).subscribe(slots => {
      this.availableSlots = slots;
      this.changeState('modificando');
    });
  }

  confirmChange() {
    if (!this.selectedNewDate || !this.currentAppointment) return;

    const dateObj = new Date(this.selectedNewDate);
    const newFecha = dateObj.toISOString().split('T')[0]; // '2026-12-20'
    const newHora = dateObj.toTimeString().split(' ')[0]; // '10:00:00'

    this.appointmentService.updateAppointmentDate(
      this.currentAppointment.idCita,
      newFecha,
      newHora
    ).subscribe({
      next: (resp) => {
        // Actualizamos la vista
        this.formatViewDetails(newFecha, newHora);
        this.changeState('confirmado');
      },
      error: () => alert('Error al modificar la cita.')
    });
  }

  cancelAppointment() {
    if (!this.currentAppointment) return;
    if (confirm('¿Seguro que deseas cancelar? Esta acción es irreversible.')) {
      this.appointmentService.cancelAppointment(this.currentAppointment.idCita).subscribe({
        next: () => this.changeState('cancelado'),
        error: () => alert('Error al cancelar la cita.')
      });
    }
  }

  // --- UTILIDADES ---

  // Lógica de negocio: Traducir Tamaño -> Minutos
  private calculateDuration(tamanio: string): number {
    switch (tamanio) {
      case 'MINI': return 30;       // 30 min
      case 'PEQUEÑO': return 60;    // 1 hora
      case 'MEDIANO': return 120;   // 2 horas
      case 'GRANDE': return 240;    // 4 horas
      case 'MUY_GRANDE': return 360;// 6 horas
      default: return 60;           // Por defecto 1 hora
    }
  }

  private formatViewDetails(fecha: string, hora: string) {
    const fullDate = new Date(`${fecha}T${hora}`);

    // Formateadores
    const weekdayFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' });
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });

    // Convertir minutos a texto legible (ej: 120 -> "2 horas")
    const horas = Math.floor(this.calculatedDuration / 60);
    const minRestantes = this.calculatedDuration % 60;
    let durText = '';
    if (horas > 0) durText += `${horas} h `;
    if (minRestantes > 0) durText += `${minRestantes} min`;

    this.viewDetails = {
      weekday: weekdayFormatter.format(fullDate),
      day: dayFormatter.format(fullDate).toUpperCase(),
      year: fullDate.getFullYear().toString(),
      hour: hora.substring(0, 5), // '16:00'
      durationText: `(${durText.trim()})`
    };
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
