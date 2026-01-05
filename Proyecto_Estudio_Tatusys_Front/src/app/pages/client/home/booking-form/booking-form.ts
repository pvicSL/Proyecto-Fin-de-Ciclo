import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';

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
  currentAppointmentId: number | null = null;

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

  // Mapea los value="body-..." del HTML a los ENUM de Java
  private readonly MAPA_ZONAS: Record<string, string> = {
    'body-arm': 'BRAZO',
    'body-forearm': 'ANTEBRAZO',
    'body-elbow': 'CODO',
    'body-shoulder': 'HOMBRO',
    'body-chest': 'TÓRAX',      // Cuidado con la tilde, Java debe esperarla así
    'body-abdomen': 'ABDOMEN',
    'body-pubis': 'PUBIS',
    'body-thigh': 'MUSLO',
    'body-knee': 'RODILLA',
    'body-calf': 'PANTORILLA',  // Errata conocida en tu Back
    'body-foot': 'PIE',
    'body-hand': 'MANO',
    'body-cervical': 'CERVIAL', // Errata conocida en tu Back
    'body-lumbar': 'LUMBARES',
    'body-buttcheek': 'NALGA',
    'body-head': 'CABEZA'
  };

  private readonly MAPA_SERVICIOS: Record<string, string> = {
    'tattoo-new': 'TATUAJE',
    'tattoo-delete': 'ELIMINACION', // Ojo: en tu HTML es tattoo-delete
    'cover-up': 'COVER',
    'tattoo-retouch': 'RETOQUE'     // Ojo: en tu HTML es tattoo-retouch
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
    'Japonés': 'JAPONES',       // Quitamos tilde para el Back
    'Lettering': 'LETERING',    // Tu Back tiene LETERING (una T)
    'Fineline': 'FINELINE',
    'Black And Grey': 'BLACKANDGREY',
    'Anime': 'ANIME'
  };

  // Mapa especial para DETALLE (HTML Value -> Java Enum)
  // Como tu HTML envía frases largas, mapeamos la frase completa o la primera palabra
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
  // 5. LÓGICA DEL CALENDARIO
  // ==========================================

  get currentMonthName(): string {
    return new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(this.currentViewDate).toUpperCase();
  }

  private escucharCambiosParaDisponibilidad() {
    this.bookingForm.valueChanges.subscribe(valores => {
      if (valores.service && valores.size && valores.detailLevel) {
        // Podrías ajustar esto según el tamaño real
        const duracionEstimada = this.calcularDuracionSegunTamano(this.mapearTamanio(valores.size));
        this.cargarHuecosBackend(duracionEstimada);
      }
    });
  }

  private cargarHuecosBackend(duracion: number) {
    this.appointmentService.getAvailableSlots(duracion).subscribe({
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

    // NUEVO: Calculamos la fecha mínima (Hoy + 3 días de margen para fianza)
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3); // Días de margen

    const startOfWeek = this.getMonday(this.currentViewDate);

    for (let i = 0; i < 7; i++) {
      const iterDate = new Date(startOfWeek);
      iterDate.setDate(startOfWeek.getDate() + i);

      const iterDateString = iterDate.toISOString().split('T')[0];
      const isWeekend = (iterDate.getDay() === 0 || iterDate.getDay() === 6);

      // CAMBIO: Ahora 'isPast' incluye todo lo que sea anterior a la fecha mínima (+3 días)
      // Usamos setHours(0,0,0,0) para comparar solo fechas sin horas
      const iterDateMidnight = new Date(iterDate);
      iterDateMidnight.setHours(0, 0, 0, 0);

      const isLocked = iterDateMidnight < minDate; // Bloqueado por regla de fianza o pasado

      const slots = this.backendSlotsMap.get(iterDateString) || [];
      const hasSlots = slots.length > 0;

      let status: CalendarDay['status'] = 'empty';

      if (isLocked) status = 'disabled'; // Ahora esto cubre pasado y los próximos 3 días
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

  // Navegación
  prevMonth() { this.navigateMonth(-1); }
  nextMonth() { this.navigateMonth(1); }

  private navigateMonth(direction: number) {
    const newDate = new Date(this.currentViewDate);
    newDate.setMonth(newDate.getMonth() + direction);

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
    this.currentAppointmentId = null;
    this.selectedFiles = [];
    const fileInput = document.getElementById('references') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  // ==========================================
  // 7. ENVÍO (SUBMIT) - ¡LÓGICA BLINDADA!
  // ==========================================

  onSubmit() {
    if (this.bookingForm.valid && this.selectedSlot && this.selectedDateStr) {
      const raw = this.bookingForm.value;

      // Usamos las funciones de mapeo seguras
      const citaObjeto = {
        tipo: this.mapearServicio(raw.service),
        zona: this.mapearZona(raw.bodyZone),
        tamanio: this.mapearTamanio(raw.size),
        detalle: this.mapearDetalle(raw.detailLevel), // Aquí usa el mapa de frases largas
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
          email: raw.email.toLowerCase(), // <--- CAMBIO: Forzamos minúsculas
          telefono: raw.phone,
          documentoIdentificacion: raw.cif
        },
        idCita: this.currentAppointmentId ? this.currentAppointmentId : undefined
      };

      // Construcción del FormData
      const formData = new FormData();
      formData.append('cita', JSON.stringify(citaObjeto));
      this.selectedFiles.forEach(file => formData.append('ficheros', file));

      console.log('Enviando...', citaObjeto);

      this.appointmentService.createAppointment(formData).subscribe({
        next: (res) => {
          alert('¡Solicitud enviada correctamente!');
          this.resetForm();
          this.isFormOpen = false;
        },
        error: (err) => {
          console.error(err);
          // Mostramos un error amigable si es 400
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
  // 8. EDICIÓN (Cargar datos) - ¡MAPEO INVERSO!
  // ==========================================

  cargarDatosParaEditar(cita: any) {
    this.isFormOpen = true;
    this.currentAppointmentId = cita.idCita;

    // Aquí convertimos de BBDD (Enum) a HTML (Value/Text)
    this.bookingForm.patchValue({
      firstName: cita.cliente.nombre,
      lastSurname: cita.cliente.apellido1,
      secondSurname: cita.cliente.apellido2,
      email: cita.cliente.email,
      phone: cita.cliente.telefono,
      cif: cita.cliente.documentoIdentificacion,
      comments: cita.comentarios,
      needInvoice: cita.factura === 1,
      acceptTerms: true,

      // Búsqueda inversa: Buscamos qué clave del mapa produce el valor que tenemos
      size: this.getKeyByValue(this.MAPA_TAMANIOS, cita.tamanio) || '',
      service: this.getKeyByValue(this.MAPA_SERVICIOS, cita.tipo) || '',
      bodyZone: this.getKeyByValue(this.MAPA_ZONAS, cita.zona) || '', // Ej: devuelve 'body-arm' si cita.zona es 'BRAZO'

      // Estos son especiales (Textos)
      style: this.getKeyByValue(this.MAPA_ESTILOS, cita.estilo) || 'Realismo',
      detailLevel: this.getKeyByValue(this.MAPA_DETALLES, cita.detalle) || '', // Devuelve la frase larga

      colorMode: cita.coloracion === 'COLOR' ? 'color' : 'bw'
    });

    if (cita.fecha) {
      this.currentViewDate = new Date(cita.fecha);
      const duracion = this.calcularDuracionSegunTamano(cita.tamanio); // Usa valor BBDD directo

      this.appointmentService.getAvailableSlots(duracion).subscribe(data => {
        this.backendSlotsMap = new Map(Object.entries(data));
        this.generateWeekGrid();

        this.selectedDateStr = cita.fecha.toString();
        this.selectedSlot = cita.hora.toString().substring(0, 5);

        const dayFound = this.weekDaysToDisplay.find(d => d.dateStr === this.selectedDateStr);
        if (dayFound) {
          this.selectedDayIndex = this.weekDaysToDisplay.indexOf(dayFound);
        }
      });
    }
    this.toggleForm();
  }

  // ==========================================
  // 9. HELPERS DE MAPEO (¡Clave para que no falle!)
  // ==========================================

  emailMatchValidator(form: AbstractControl): ValidationErrors | null {
    const e = form.get('email')?.value;
    const c = form.get('confirmEmail')?.value;
    return (e && c && e === c) ? null : { emailsDontMatch: true };
  }

  // Mapeadores seguros: si no encuentran el valor, devuelven algo por defecto o el mismo valor
  // Esto evita enviar 'undefined' al servidor.

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
    // Si encuentra la frase entera en el mapa, devuelve el ENUM.
    // Si no (por si acaso), coge la primera palabra y la pone en mayúsculas.
    return this.MAPA_DETALLES[valorHTML] || valorHTML.split(' ')[0].toUpperCase();
  }

  // Helper para buscar clave por valor (Reverse Lookup para Edición)
  private getKeyByValue(object: any, value: string): string | undefined {
    return Object.keys(object).find(key => object[key] === value);
  }

  private calcularDuracionSegunTamano(tamanioBBDD: string): number {
    switch (tamanioBBDD) {
      case 'MINI': return 60;
      case 'PEQUEÑO': return 90;
      case 'GRANDE': return 180;
      case 'MUY_GRANDE': return 240;
      default: return 120; // MEDIANO
    }
  }
}