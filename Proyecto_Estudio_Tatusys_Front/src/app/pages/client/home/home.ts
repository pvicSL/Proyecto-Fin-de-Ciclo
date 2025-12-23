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
  // 1. Declaramos la propiedad 'viewMode' para solucionar el error TS2339.
  // Le damos tipo literal para que solo acepte esos dos valores.
  viewMode: 'booking' | 'modifying' = 'booking';

  toggleMode() {
    // 2. Esta función alterna entre los dos estados
    this.viewMode = this.viewMode === 'booking' ? 'modifying' : 'booking';

    // Un console.log te ayudará a verificar en la consola del navegador si el botón funciona
    console.log('Modo actual:', this.viewMode);
  }
}