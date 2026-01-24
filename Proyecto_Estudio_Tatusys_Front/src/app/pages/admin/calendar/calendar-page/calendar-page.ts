import { Component, OnInit, ViewChild, } from '@angular/core';
import { CalendarOptions, DatesSetArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import esLocale from '@fullcalendar/core/locales/es'; // Para ponerlo en español
import { Router } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { FullCalendarComponent, FullCalendarModule } from '@fullcalendar/angular';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-calendar-page',
  imports: [CommonModule, FullCalendarModule],
  templateUrl: './calendar-page.html',
  styleUrl: './calendar-page.css',
})
export class CalendarPage {

  @ViewChild('calendar') calendarComponent!: FullCalendarComponent;
  mesActual: string = '';

  calendarOptions: CalendarOptions = {
    initialView: 'dayGridMonth',
    plugins: [dayGridPlugin],
    locale: esLocale,
    headerToolbar: false,
    // Este evento se lanza al cargar y cada vez que cambias de mes
    datesSet: (info: DatesSetArg) => {
      this.actualizarTituloMes();
      this.cargarCitasDesdeCalendario(info);
    },
    eventClick: (info) => this.navegarADetalle(info.event.id)
  };

  constructor(
    private appointmentService: AppointmentService,
    private router: Router
  ) {}

  /**
   * Este método extrae la fecha que el calendario está mostrando 
   * y llama a tu servicio con los parámetros requeridos.
   */
  cargarCitasDesdeCalendario(info: DatesSetArg) {
    // 1. Obtenemos la fecha central del rango visible para saber en qué mes estamos
    // Usamos info.view.currentStart que es el primer día del mes actual
    const fechaISO = info.view.currentStart.toISOString().split('T')[0];
    
    // 2. Llamamos a tu método con los dos parámetros: fecha y vista
    // Enviamos "mes" porque esta página es específicamente para la vista mensual
    this.appointmentService.getConfirmedAppointments(fechaISO, 'mes').subscribe({
      next: (citas) => {
        this.calendarOptions.events = citas.map(c => ({
          id: c.idCita.toString(),
          title: `${c.hora} - ${c.clienteNombre}`,
          start: c.fecha,
          className: 'cita-badge'
        }));
      },
      error: (err) => console.error('Error al cargar citas mensuales', err)
    });
  }

  // --- Métodos de navegación (Flechas) ---

  irAlMesAnterior() {
    this.calendarComponent.getApi().prev();
  }

  irAlMesSiguiente() {
    this.calendarComponent.getApi().next();
  }

  actualizarTituloMes() {
    this.mesActual = this.calendarComponent.getApi().view.title;
  }

  navegarADetalle(id: string) {
    this.router.navigate(['admin/citas/detalleCitaConfirmada', id]);
  }
}

