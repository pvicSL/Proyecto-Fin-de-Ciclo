import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; // Necesario para el [(ngModel)] del select

type ModifierState = 'search' | 'inicial' | 'modificando' | 'confirmado' | 'cancelado';

@Component({
  selector: 'app-appointment-modifier',
  standalone: true,
  imports: [CommonModule, FormsModule], // Importamos FormsModule para inputs simples
  templateUrl: './appointment-modifier.html',
  styleUrl: './appointment-modifier.css'
})
export class AppointmentModifierComponent {

  // Estado actual de la tarjeta
  currentState: ModifierState = 'search';

  bookingCode: string = '';

  // Datos simulados de la nueva fecha seleccionada
  selectedNewDate: string = '';

  // Datos para mostrar en la confirmación
  newAppointmentDetails = {
    weekday: '',
    day: '',
    year: '',
    hour: ''
  };

  // --- MÉTODOS DE CAMBIO DE ESTADO ---



  changeState(newState: ModifierState) {
    this.currentState = newState;
    // Scroll suave hacia la tarjeta al cambiar de estado
    setTimeout(() => {
      document.getElementById('modifier-card')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 100);
  }

  cancelAppointment() {
    const confirmacion = confirm('¿Seguro que deseas cancelar tu cita? Perderás la fianza.');
    if (confirmacion) {
      this.changeState('cancelado');
    }
  }

  confirmChange() {
    if (!this.selectedNewDate) return;

    // Lógica simple para formatear la fecha elegida (Simulación)
    const dateObj = new Date(this.selectedNewDate);

    // Formateadores de fecha en español
    const weekdayFormatter = new Intl.DateTimeFormat('es-ES', { weekday: 'long' });
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });
    const timeFormatter = new Intl.DateTimeFormat('es-ES', { hour: '2-digit', minute: '2-digit' });

    this.newAppointmentDetails = {
      weekday: weekdayFormatter.format(dateObj),
      day: dayFormatter.format(dateObj).toUpperCase(),
      year: dateObj.getFullYear().toString(),
      hour: timeFormatter.format(dateObj)
    };

    this.changeState('confirmado');
  }

  // Este método emitirá un evento al padre (Home) para cerrar este componente
  closeModifier() {
    // Por ahora, simplemente recargamos la página o ocultamos (lo conectaremos en el Home luego)
    // Para simplificar hoy:
    this.currentState = 'inicial';
    // Aquí idealmente emitiríamos un evento @Output hacia arriba
    alert('Aquí se cerraría el modificador y volvería al inicio.');
  }
}