import { Component } from '@angular/core';
import { BookingFormComponent } from './booking-form/booking-form';
import { AppointmentModifierComponent } from './appointment-modifier/appointment-modifier';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, BookingFormComponent, AppointmentModifierComponent], // <--- AÑADIDOS
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class HomeComponent {
  // Variable para saber qué mostrar
  viewMode: 'booking' | 'modifying' = 'booking';

  toggleMode() {
    this.viewMode = this.viewMode === 'booking' ? 'modifying' : 'booking';
  }
}
