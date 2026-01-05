import { Component, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';

// Estados posibles para el recuadro de modificación o cancelación de una cita
type ModifierState = 'search' | 'inicial' | 'modificando' | 'confirmado' | 'cancelado';


// Interfaz para las columnas de días del selector de fechas
interface DayColumn {
  dateObj: Date;
  dateStr: string; // Por ejemplo: "2026-12-20"
  weekday: string; // Por ejemplo: "Lunes"
  dayNumber: string; // Por ejemplo: "20"
  slots: string[]; //Esto carga el array de huecos disponibles
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

  // Búsqueda de la cita mediante su clave, es el estado inicial
  currentState: ModifierState = 'search';

  // VARIABLES DE BÚSQUEDA
  bookingCode: string = '';  // Referencia (A1B2...)
  bookingEmail: string = ''; // NUEVO: Email (usuario@mail...)

  currentAppointment: AppointmentDTO | null = null;
  calculatedDuration: number = 0; // Duración en minutos calculada con los parámetros

  selectedNewDate: string = '';
  availableSlots: string[] = [];

  viewDetails = {
    weekday: '',
    day: '',
    year: '',
    hour: '',
    durationText: '' // Para mostrar "(2 horas)" en lugar de "x minutos"
  };

  // VARIABLES PARA EL CALENDARIO SEMANAL
  allDays: DayColumn[] = []; // Todos los días que devuelve el back (Ajustar a 60)??
  weekIndex: number = 0;     // En qué semana estamos (0, 1, 2...)
  daysPerPage: number = 7;   // Días que se ven por pantalla (vista de semana)

  // Constructor del service
  constructor(private appointmentService: AppointmentService) { }

  // --- MÉTODOS DE CAMBIO DE ESTADO ---
  formatSlotDate(isoDate: string): string {
    // PROTECCIÓN: si la fecha viene con espacio "2026-12-20 10:00", 
    // la cambiamos a "2026-12-20T10:00" para que new Date() no falle nunca.
    const safeDate = isoDate.replace(' ', 'T');

    const date = new Date(safeDate);

    // Se valida que la fecha sea válida antes de darle formato
    if (isNaN(date.getTime())) return isoDate;

    //Formateo de la fecha y de la hora
    const dayFormatter = new Intl.DateTimeFormat('es-ES', { day: '2-digit', month: 'short' });
    const timeFormatter = new Intl.DateTimeFormat('es-ES', { hour: '2-digit', minute: '2-digit' });

    // Devuelve algo como: "20 DIC - 10:00"
    return `${dayFormatter.format(date).toUpperCase()} - ${timeFormatter.format(date)}`;
  }

  searchAppointment() {
    // Validamos que ambos campos tengan algo
    if (!this.bookingCode.trim() || !this.bookingEmail.trim()) return;

    // Llamamos al método nuevo del servicio que creamos antes
    // getAppointmentByLocator(ref, email)
    this.appointmentService.getAppointmentByLocator(this.bookingCode, this.bookingEmail.toLowerCase()).subscribe({
      next: (data) => {
        this.currentAppointment = data;

        if (data.estatus === 'RECHAZADO' || data.estatus === 'CANCELADO') {
          alert('Esta cita ya figura como cancelada.');
          this.changeState('cancelado');
          return;
        }

        // Calculamos duración y preparamos la vista
        this.calculatedDuration = this.calculateDuration(data.tamanio);
        this.formatViewDetails(data.fecha, data.hora);

        this.changeState('inicial');
      },
      error: (err) => {
        console.error(err);
        // Mensaje genérico de seguridad: No decimos si falló el email o el código
        alert('No se ha encontrado ninguna cita con esos datos. Verifica el localizador y el email.');
      }
    });
  }

  // --- MODIFICA ESTE MÉTODO ---
  startModification() {
    // 1. Calculamos duración
    this.calculatedDuration = this.calculateDuration(this.currentAppointment?.tamanio || 'MEDIANO');

    console.log("Buscando huecos...", this.calculatedDuration);

    // 2. Llamamos al Back
    this.appointmentService.getAvailableSlots(this.calculatedDuration).subscribe({
      next: (dataBackend) => {
        // TRANSFORMA LOS DATOS: De Mapa JSON a Array ordenado de objetos
        this.allDays = this.transformarMapaADias(dataBackend);
        this.weekIndex = 0; // Reseteamos a la primera semana
        this.selectedNewDate = ''; // Reseteamos selección
        this.changeState('modificando');
      },
      error: (err) => alert("Error cargando disponibilidad")
    });
  }

  // --- MÉTODOS DE NAVEGACIÓN Y VISUALIZACIÓN ---

  // Obtiene solo los 7 días de la semana actual para pintar en el HTML
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
    // Guardamos la fecha completa: "2026-12-20 10:00"
    this.selectedNewDate = `${day.dateStr} ${slotTime}`;
  }

  // Comprueba si un hueco está seleccionado para pintarlo de otro color
  isSlotSelected(day: DayColumn, slotTime: string): boolean {
    return this.selectedNewDate === `${day.dateStr} ${slotTime}`;
  }

  // --- FUNCIÓN AUXILIAR DE TRANSFORMACIÓN ---
  private transformarMapaADias(dataBackend: any): DayColumn[] {
    const diasTemp: DayColumn[] = [];
    const formatterDia = new Intl.DateTimeFormat('es-ES', { weekday: 'short' });

    // NUEVO: Fecha mínima hoy + 3 días
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3);

    // Recorremos las claves (fechas)
    for (const fechaStr in dataBackend) {
      if (dataBackend.hasOwnProperty(fechaStr)) {
        const fechaObj = new Date(fechaStr);
        // Quitamos horas para comparar fechas puras
        const fechaObjMidnight = new Date(fechaObj);
        fechaObjMidnight.setHours(0, 0, 0, 0);

        // SOLO añadimos el día si es igual o posterior a la fecha mínima
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

    // Ordenamos cronológicamente
    return diasTemp.sort((a, b) => a.dateObj.getTime() - b.dateObj.getTime());
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
