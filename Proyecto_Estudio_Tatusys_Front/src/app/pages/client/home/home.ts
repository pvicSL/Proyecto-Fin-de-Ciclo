import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
// Asegúrate de que estas rutas coincidan exactamente con el nombre de tus archivos
import { BookingFormComponent } from './booking-form/booking-form';
import { AppointmentModifierComponent } from './appointment-modifier/appointment-modifier';

@Component({
  selector: 'app-home',
  standalone: true,
  // CommonModule es necesario para que funcionen *ngIf y *ngFor
  imports: [CommonModule, BookingFormComponent, AppointmentModifierComponent],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent {
  // 1. 'viewMode' sirve para alternar entre la solicitud de cita o la modificación.
  viewMode: 'booking' | 'modifying' = 'booking';

  toggleMode() {
    // 2. Esta función alterna entre los dos estados,
    // de modo que podamos ocultar elementos según qué esté haciendo el usuario.
    this.viewMode = this.viewMode === 'booking' ? 'modifying' : 'booking';

    // Verificación por consola de que esto funciona
    console.log('Modo/estado actual:', this.viewMode);
  }
}