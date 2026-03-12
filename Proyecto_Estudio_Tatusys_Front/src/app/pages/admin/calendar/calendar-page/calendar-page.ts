import { Component, HostListener, OnInit, ViewChild, } from '@angular/core';
import { CalendarOptions, DatesSetArg, EventClickArg } from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import listPlugin from '@fullcalendar/list';
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
    height: 'auto',
    expandRows: true,
    displayEventTime: false,
    headerToolbar: false,
    handleWindowResize: true, // El calendario se redibuja al girar el móvil
    aspectRatio: 1.35,        // Relación ancho/alto (ajusta según prefieras)
    dayMaxEvents: true,       // Si hay muchas citas, pone un "+2 más" en lugar de estirar la celda
    
    
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
  // 1. Obtenemos el objeto fecha actual del calendario (ej: Marzo 2026)
  const fechaCalendario = this.calendarComponent.getApi().getDate();
  
  // 2. Construimos manualmente el "YYYY-MM-01"
  // Usamos getFullYear y getMonth (+1 porque empieza en 0)
  const anio = fechaCalendario.getFullYear();
  const mes = (fechaCalendario.getMonth() + 1).toString().padStart(2, '0');
  const primerDiaMes = `${anio}-${mes}-01`;

  console.log('Solicitando rango al Backend:', primerDiaMes, 'Vista: mes');

  this.appointmentService.getConfirmedAppointments(primerDiaMes, 'mes').subscribe({
    next: (citas) => {

      const nuevosEventos = citas.map(c => ({
        id: String(c.idCita), 
        
        // El título queda limpio: solo lo que el tatuador necesita ver
        title: `${c.hora} - ${c.clienteNombre}`, 
        
        start: c.fecha,
        allDay: false,
        backgroundColor: '#198754',
        borderColor: '#146c43',
        className: 'cita-confirmada' // Para darle estilos CSS si quieres
      }));

      this.calendarOptions = { ...this.calendarOptions, events: nuevosEventos };
    },
    error: (err) => console.error('Error en TatuSys API:', err)
  });
}

ngOnInit() {
    this.configurarVistaSegunPantalla();
  }

  configurarVistaSegunPantalla() {
    const esMovil = window.innerWidth < 768;
    
    this.calendarOptions = {
      ...this.calendarOptions,
      plugins: [dayGridPlugin, listPlugin], // Asegúrate de incluir listPlugin
      initialView: esMovil ? 'listMonth' : 'dayGridMonth',
      // Personaliza el texto de la lista si no hay eventos
      noEventsText: 'No hay citas para este mes'
    };
  }

  // Opcional: Escuchar el cambio de tamaño en tiempo real
  @HostListener('window:resize')
  onResize() {
    const api = this.calendarComponent.getApi();
    const esMovil = window.innerWidth < 768;
    const vistaActual = api.view.type;

    if (esMovil && vistaActual !== 'listMonth') {
      api.changeView('listMonth');
    } else if (!esMovil && vistaActual !== 'dayGridMonth') {
      api.changeView('dayGridMonth');
    }
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
    if (id && id !== '0') {
      // Te lleva a la página donde está el botón "Finalizar Trabajo"
      this.router.navigate(['admin/citas/detalleCitaConfirmada', id]);
    } else {
      console.error("Error: Se intentó navegar con un ID no válido");
    }
  }
}

