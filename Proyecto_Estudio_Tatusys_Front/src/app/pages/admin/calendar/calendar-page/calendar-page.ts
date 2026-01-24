import { Component, OnInit, ViewChild, } from '@angular/core';
import { CalendarOptions, DatesSetArg, EventClickArg } from '@fullcalendar/core';
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
    eventClick: (info: EventClickArg) => this.navegarADetalle(info.event.id)
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
  const fechaISO = info.view.currentStart.toISOString().split('T')[0];
  
  this.appointmentService.getConfirmedAppointments(fechaISO, 'mes').subscribe({
    next: (citas) => {
      // 1. Mapeamos los datos
      const nuevosEventos = citas.map(c => ({
        id: c.idCita.toString(),
        title: `${c.hora} - ${c.clienteNombre}`,
        start: c.fecha, // Asegúrate que c.fecha sea 'YYYY-MM-DD'
        className: 'cita-badge'
      }));

      // 2. IMPORTANTE: Reasignamos el objeto de opciones para que Angular detecte el cambio
      this.calendarOptions = {
        ...this.calendarOptions,
        events: nuevosEventos
      };

      console.log('Citas cargadas con éxito:', nuevosEventos);
    },
    error: (err) => console.error('Error al cargar citas:', err)
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

