import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { debounceTime, filter, switchMap, map } from 'rxjs/operators'; // <--- Añadido 'map'

// Interfaz para tipar correctamente los días del calendario
interface CalendarDay {
  dateObj: Date;
  dateStr: string;
  dayNumber: number;
  weekdayLetter: string;
  status: 'available' | 'disabled' | 'weekend' | 'empty' | 'none';
  slots: string[];
}

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './booking-form.html',
  styleUrl: './booking-form.css'
})
export class BookingFormComponent implements OnInit {

  // ==========================================
  // 1. VARIABLES DE ESTADO Y UI
  // ==========================================
  isFormOpen: boolean = false;
  activeHint: string | null = null;

  // ==========================================
  // 2. FORMULARIO Y DATOS
  // ==========================================
  bookingForm: FormGroup;
  selectedFiles: File[] = [];

  // ==========================================
  // 3. VARIABLES DEL CALENDARIO
  // ==========================================
  currentViewDate: Date = new Date();
  weekDaysToDisplay: CalendarDay[] = [];
  backendSlotsMap: Map<string, string[]> = new Map();

  selectedDayIndex: number | null = null;
  selectedDateStr: string | null = null;
  selectedSlot: string | null = null;

  // ==========================================
  // 4. MAPAS DE TRADUCCIÓN (HTML -> BBDD)
  // ==========================================

  private readonly MAPA_ZONAS: Record<string, string> = {
    'body-arm': 'BRAZO',
    'body-forearm': 'ANTEBRAZO',
    'body-elbow': 'CODO',
    'body-shoulder': 'HOMBRO',
    'body-chest': 'TÓRAX',
    'body-abdomen': 'ABDOMEN',
    'body-pubis': 'PUBIS',
    'body-thigh': 'MUSLO',
    'body-knee': 'RODILLA',
    'body-calf': 'PANTORILLA',
    'body-foot': 'PIE',
    'body-hand': 'MANO',
    'body-cervical': 'CERVICAL',
    'body-lumbar': 'LUMBARES',
    'body-buttcheek': 'NALGA',
    'body-head': 'CABEZA'
  };

  private readonly MAPA_SERVICIOS: Record<string, string> = {
    'tattoo-new': 'TATUAJE',
    'tattoo-delete': 'ELIMINACION',
    'cover-up': 'COVER',
    'tattoo-retouch': 'RETOQUE'
  };

  private readonly MAPA_TAMANIOS: Record<string, string> = {
    'mini': 'MINI',
    'small': 'PEQUEÑO',
    'medium': 'MEDIANO',
    'large': 'GRANDE',
    'xtralarge': 'MUY_GRANDE'
  };

  private readonly MAPA_ESTILOS: Record<string, string> = {
    'Realismo': 'REALISMO',
    'Tradicional': 'TRADICIONAL',
    'Japonés': 'JAPONES',
    'Lettering': 'LETERING',
    'Fineline': 'FINELINE',
    'Black And Grey': 'BLACKANDGREY',
    'Anime': 'ANIME'
  };

  private readonly MAPA_DETALLES: Record<string, string> = {
    'Sencillo (líneas finas o poca saturación)': 'SENCILLO',
    'Medio (líneas medias o más saturación)': 'MEDIO',
    'Denso (líneas gruesas o mucha saturación)': 'DENSO'
  };

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService
  ) {
    this.bookingForm = this.fb.group({
      firstName: ['', Validators.required],
      lastSurname: ['', Validators.required],
      secondSurname: [''],
      email: ['', [Validators.required, Validators.email]],
      confirmEmail: ['', Validators.required],
      phone: ['', Validators.required],
      needInvoice: [false],
      cif: [''],
      billingAddress: [''],
      service: ['', Validators.required],
      bodyZone: ['', Validators.required],
      size: ['', Validators.required],
      style: ['', Validators.required],
      detailLevel: ['', Validators.required],
      colorMode: ['bw', Validators.required],
      comments: [''],
      references: [''],
      acceptTerms: [false, Validators.requiredTrue]
    }, { validators: this.emailMatchValidator });

    this.escucharCambiosParaDisponibilidad();
  }

  ngOnInit(): void { }

  // ==========================================
  // 5. LÓGICA DEL CALENDARIO Y DISPONIBILIDAD
  // ==========================================

  get currentMonthName(): string {
    return new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(this.currentViewDate).toUpperCase();
  }

  // Escucha cambios en el formulario para preguntar al Back la duración
  private escucharCambiosParaDisponibilidad() {
    this.bookingForm.valueChanges.pipe(
      debounceTime(500), // Espera a que termine de escribir
      // Solo procede si los campos que definen la duración están llenos
      filter(val => val.service && val.size && val.detailLevel && val.colorMode),
      switchMap(val => {
        // Preparamos TODOS los criterios (incluyendo los que usarás para filtrar por empleado)
        const criterios = {
          tamanio: this.mapearTamanio(val.size),
          detalle: this.mapearDetalle(val.detailLevel),
          coloracion: val.colorMode === 'bw' ? 'NEGRO' : 'COLOR',
          // Estos dos campos serán clave para asignar empleado en el futuro
          tipo: this.mapearServicio(val.service),
          estilo: this.mapearEstilo(val.style)
        };

        // Llama al endpoint de cálculo de duración
        // Usamos pipe(map) para no perder los 'criterios' al obtener la 'duracion'
        return this.appointmentService.calculateDuration(criterios).pipe(
          map(duracion => ({ duracion, criterios }))
        );
      })
    ).subscribe({
      next: (resultado) => {
        console.log('Duración:', resultado.duracion, 'minutos. Criterios:', resultado.criterios);


        // Aquí SÍ pasamos los criterios a la función local. 
        // Como la función local 'cargarHuecosBackend' ahora acepta el segundo parámetro (aunque no lo use),
        // esto NO dará error.
        this.cargarHuecosBackend(resultado.duracion, resultado.criterios);
      },
      error: (err) => console.error('Error calculando duración', err)
    });
  }

  // MODIFICADO: Acepta el parámetro 'filtros' para que no falle la llamada desde 'escucharCambios...'
  // pero lo marcamos como opcional (?)
  private cargarHuecosBackend(duracion: number, filtros?: { tipo: string, estilo: string }) {

    // LLAMADA AL SERVICIO:
    // Dejamos el argumento 'filtros' COMENTADO dentro de la llamada.
    // Así TypeScript solo ve 1 argumento y no se queja, pero tú ves que ahí va el segundo.

    this.appointmentService.getAvailableSlots(duracion /* , filtros */).subscribe({
      next: (respuestaBackend) => {
        this.backendSlotsMap = new Map(Object.entries(respuestaBackend));
        this.generateWeekGrid();
      },
      error: (err) => console.error('Error cargando huecos', err)
    });
  }

  generateWeekGrid() {
    this.weekDaysToDisplay = [];
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3); // 3 Días de margen

    const startOfWeek = this.getMonday(this.currentViewDate);

    for (let i = 0; i < 7; i++) {
      const iterDate = new Date(startOfWeek);
      iterDate.setDate(startOfWeek.getDate() + i);

      const iterDateString = iterDate.toISOString().split('T')[0];
      const isWeekend = (iterDate.getDay() === 0 || iterDate.getDay() === 6);

      const iterDateMidnight = new Date(iterDate);
      iterDateMidnight.setHours(0, 0, 0, 0);

      const isLocked = iterDateMidnight < minDate;

      const slots = this.backendSlotsMap.get(iterDateString) || [];
      const hasSlots = slots.length > 0;

      let status: CalendarDay['status'] = 'empty';

      if (isLocked) status = 'disabled';
      else if (isWeekend) status = 'weekend';
      else if (hasSlots) status = 'available';

      this.weekDaysToDisplay.push({
        dateObj: iterDate,
        dateStr: iterDateString,
        dayNumber: iterDate.getDate(),
        weekdayLetter: ['D', 'L', 'M', 'X', 'J', 'V', 'S'][iterDate.getDay()],
        status: status,
        slots: slots
      });
    }
  }

  // Navegación Calendario
  prevMonth() { this.navigateMonth(-1); }
  nextMonth() { this.navigateMonth(1); }

  private navigateMonth(direction: number) {
    const newDate = new Date(this.currentViewDate);
    newDate.setMonth(newDate.getMonth() + direction);

    // Evitar ir al pasado más allá del mes actual
    const today = new Date();
    if (direction < 0 && newDate.getMonth() < today.getMonth() && newDate.getFullYear() <= today.getFullYear()) {
      return;
    }
    this.currentViewDate = newDate;
    this.generateWeekGrid();
  }

  prevWeek() { this.navigateWeek(-7); }
  nextWeek() { this.navigateWeek(7); }

  private navigateWeek(days: number) {
    const d = new Date(this.currentViewDate);
    d.setDate(d.getDate() + days);
    this.currentViewDate = d;
    this.generateWeekGrid();
  }

  private getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(date.setDate(diff));
  }

  selectDay(day: CalendarDay) {
    if (day.status !== 'available') return;
    this.selectedDayIndex = this.weekDaysToDisplay.indexOf(day);
    this.selectedDateStr = day.dateStr;
    this.selectedSlot = null;
  }

  selectSlot(time: string) {
    this.selectedSlot = time;
  }

  // ==========================================
  // 6. GESTIÓN DEL FORMULARIO Y ARCHIVOS
  // ==========================================

  toggleForm() {
    this.isFormOpen = !this.isFormOpen;
    if (this.isFormOpen) {
      setTimeout(() => {
        const element = document.getElementById('form-anchor');
        if (element) element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }, 100);
    }
  }

  toggleHint(field: string) {
    this.activeHint = this.activeHint === field ? null : field;
  }

  onFileChange(event: any) {
    const files = event.target.files;
    if (files.length > 3) {
      alert('Máximo de 3 imágenes.');
      event.target.value = '';
      this.selectedFiles = [];
      return;
    }
    this.selectedFiles = Array.from(files);
  }

  resetForm() {
    this.bookingForm.reset({ colorMode: 'bw', needInvoice: false, acceptTerms: false });
    this.selectedSlot = null;
    this.selectedDateStr = null;
    this.selectedDayIndex = null;
    this.weekDaysToDisplay = [];
    this.backendSlotsMap.clear();
    this.selectedFiles = [];

    // Limpiar input file manualmente
    const fileInput = document.getElementById('references') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  // ==========================================
  // 7. ENVÍO (SUBMIT) - SOLO CREACIÓN
  // ==========================================

  onSubmit() {
    if (this.bookingForm.valid && this.selectedSlot && this.selectedDateStr) {
      const raw = this.bookingForm.value;

      // Objeto Cita para el Backend
      const citaObjeto = {
        tipo: this.mapearServicio(raw.service),
        zona: this.mapearZona(raw.bodyZone),
        tamanio: this.mapearTamanio(raw.size),
        detalle: this.mapearDetalle(raw.detailLevel),
        coloracion: raw.colorMode === 'bw' ? 'NEGRO' : 'COLOR',
        estilo: this.mapearEstilo(raw.style),
        fecha: this.selectedDateStr,
        hora: this.selectedSlot + ":00",
        comentarios: raw.comments,
        factura: raw.needInvoice ? 1 : 0,
        estatus: 'PENDIENTE',
        cliente: {
          nombre: raw.firstName,
          apellido1: raw.lastSurname,
          apellido2: raw.secondSurname,
          email: raw.email.toLowerCase(), // Normalización importante
          telefono: raw.phone,
          documentoIdentificacion: raw.cif
        }
      };

      // Empaquetado en FormData (JSON + Ficheros)
      const formData = new FormData();
      formData.append('cita', JSON.stringify(citaObjeto));
      this.selectedFiles.forEach(file => formData.append('ficheros', file));

      console.log('Creando nueva cita...');

      this.appointmentService.createAppointment(formData).subscribe({
        next: (res) => {
          alert('¡Solicitud enviada correctamente! Recibirás un email con los detalles para la fianza.');
          this.resetForm();
          this.isFormOpen = false;
        },
        error: (err) => {
          console.error(err);
          if (err.status === 400) {
            alert('Error en los datos. Por favor revisa el formulario.');
          } else {
            alert('Error al conectar con el servidor.');
          }
        }
      });
    } else {
      this.bookingForm.markAllAsTouched();
    }
  }

  // ==========================================
  // 8. HELPERS DE MAPEO
  // ==========================================

  emailMatchValidator(form: AbstractControl): ValidationErrors | null {
    const e = form.get('email')?.value;
    const c = form.get('confirmEmail')?.value;
    return (e && c && e === c) ? null : { emailsDontMatch: true };
  }

  private mapearZona(valorHTML: string): string {
    return this.MAPA_ZONAS[valorHTML] || 'BRAZO';
  }

  private mapearServicio(valorHTML: string): string {
    return this.MAPA_SERVICIOS[valorHTML] || 'TATUAJE';
  }

  private mapearTamanio(valorHTML: string): string {
    return this.MAPA_TAMANIOS[valorHTML] || 'MEDIANO';
  }

  private mapearEstilo(valorHTML: string): string {
    return this.MAPA_ESTILOS[valorHTML] || 'REALISMO';
  }

  private mapearDetalle(valorHTML: string): string {
    return this.MAPA_DETALLES[valorHTML] || valorHTML.split(' ')[0].toUpperCase();
  }
}