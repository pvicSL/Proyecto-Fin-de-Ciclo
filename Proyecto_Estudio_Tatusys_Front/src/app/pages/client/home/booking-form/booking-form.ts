import { Component, OnInit } from '@angular/core';
// ReactiveFormsModule, para usar formGroup y formControlName
import { CommonModule, NgClass } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { debounceTime, filter, switchMap } from 'rxjs/operators';

// Definición de la estructura de un día del calendario
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
  imports: [ReactiveFormsModule, NgClass],
  templateUrl: './booking-form.html',
  styleUrl: './booking-form.css'
})
export class BookingFormComponent implements OnInit {

  // ==========================================
  // 1. VARIABLES DE ESTADO Y UI
  // ==========================================
  isFormOpen: boolean = false;
  activeHint: string | null = null;
  assignedWorkerId: number | null = null;

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
  // Mapa para guardar los huecos que devuelve el backend: Clave='2026-05-20', Valor=['10:00', '12:00']
  backendSlotsMap: Map<string, string[]> = new Map();

  selectedDayIndex: number | null = null;
  selectedDateStr: string | null = null;
  selectedSlot: string | null = null;

  // ==========================================
  // 4. MAPAS DE TRADUCCIÓN (FRONTEND -> BACKEND)
  // Para convertir los valores de HTML a los ENUMS de Spring Boot
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
    // Inicialización del Formulario Reactivo
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
      acceptTerms: [false, Validators.requiredTrue],
      acceptDataProtection: [false, Validators.requiredTrue]
    }, { validators: this.emailMatchValidator });

    // Inicialización de la escucha reactiva
    this.escucharCambiosParaDisponibilidad();
  }

  ngOnInit(): void { }

  // ==========================================
  // 5. LÓGICA REACTIVA (RXJS)
  // ==========================================

  // En lugar de un botón "Calcular", el formulario reacciona automáticamente.
  private escucharCambiosParaDisponibilidad() {
    this.bookingForm.valueChanges.pipe(
      // debounceTime(500): espera medio segundo a que el usuario deje de escribir/seleccionar.
      // Así no se hacen peticiones constantes al servidor al ir pulsando cada tecla.
      debounceTime(500),

      // filter: Solo pasamos a la siguiente fase si los campos IMPORTANTES tienen valor.
      filter(val => val.service && val.size && val.detailLevel && val.colorMode),

      // switchMap: si el usuario cambia algo mientras hay una petición pendiente, 
      // cancela la anterior y lanza la nueva.
      switchMap(val => {
        // Prepara los datos para que el backend calcule la duración estimada
        const criterios = {
          tamanio: this.mapearTamanio(val.size),
          detalle: this.mapearDetalle(val.detailLevel),
          coloracion: val.colorMode === 'bw' ? 'NEGRO' : 'COLOR',
          tipo: this.mapearServicio(val.service),
          estilo: this.mapearEstilo(val.style),
          zona: this.mapearZona(val.bodyZone)
        };

        // Se llama al servicio (devuelve un Observable)
        return this.appointmentService.calculatePreBookingData(criterios);
      })
    ).subscribe({
      next: (resultado: any) => {
        console.log('El backend ha calculado:', resultado);

        // Calculada en minutos: ej 120 minutos
        const duracion = resultado.duracion;
        // el id es un número sencillo, tipo id=5
        this.assignedWorkerId = resultado.idTrabajador;

        // Cuando se conoce la  duración y quién lo hace, se solicita su agenda.
        if (this.assignedWorkerId) {
          this.cargarHuecosBackend(duracion, this.assignedWorkerId);
        }
      },
      error: (err) => console.error('Error en el cálculo automático:', err)
    });
  }

  private cargarHuecosBackend(duracion: number, workerId: number) {
    this.appointmentService.getAvailableSlots(duracion, workerId).subscribe({
      next: (respuestaBackend) => {
        // Convierte el objeto JSON { "2026-05-20": [] } a un Map de TypeScript
        this.backendSlotsMap = new Map(Object.entries(respuestaBackend));
        // Se genera de nuevo la vista del calendario
        this.generateWeekGrid();
      },
      error: (err) => console.error('Error cargando disponibilidad:', err)
    });
  }

  // ==========================================
  // 6. LÓGICA DEL CALENDARIO
  // ==========================================
  get currentMonthName(): string {
    return new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(this.currentViewDate).toUpperCase();
  }

  generateWeekGrid() {
    this.weekDaysToDisplay = [];
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    // Norma del negocio: no se puede reservar antes de 3 días (para poder gestionar la fianza)
    const minDate = new Date(today);
    minDate.setDate(today.getDate() + 3);

    const startOfWeek = this.getMonday(this.currentViewDate);

    for (let i = 0; i < 7; i++) {
      const iterDate = new Date(startOfWeek);
      iterDate.setDate(startOfWeek.getDate() + i);

      const iterDateString = iterDate.toISOString().split('T')[0];
      // El 0 es el dominhgo y el 6 el sábado
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

  // Validaciones de navegación (para que no se pueda retroceder al pasado)
  get isPrevMonthDisabled(): boolean {
    const today = new Date();
    const currentView = new Date(this.currentViewDate.getFullYear(), this.currentViewDate.getMonth(), 1);
    const realToday = new Date(today.getFullYear(), today.getMonth(), 1);
    return currentView <= realToday;
  }

  get isPrevWeekDisabled(): boolean {
    const today = new Date();
    const viewMonday = this.getMonday(this.currentViewDate);
    viewMonday.setHours(0, 0, 0, 0);
    const realMonday = this.getMonday(today);
    realMonday.setHours(0, 0, 0, 0);
    return viewMonday <= realMonday;
  }

  prevMonth() { this.navigateMonth(-1); }
  nextMonth() { this.navigateMonth(1); }

  private navigateMonth(direction: number) {
    const newDate = new Date(this.currentViewDate);
    newDate.setMonth(newDate.getMonth() + direction);

    // Protección extra para evitar que se retroceda a fechas pasadas
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
    //Esto reinicia la hora cuando se cambia de día
    this.selectedSlot = null;
  }

  selectSlot(time: string) {
    this.selectedSlot = time;
  }

  // ==========================================
  // 7. INTERACCIÓN DE LA UI
  // ==========================================

  //Abrir y cerrar el formulario
  toggleForm() {
    this.isFormOpen = !this.isFormOpen;
    if (this.isFormOpen) {
      setTimeout(() => {
        const element = document.getElementById('form-anchor');
        if (element) element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }, 100);
    }
  }

  // Tooltips de información de opciones susceptibles de ser dudosas
  toggleHint(field: string) {
    this.activeHint = this.activeHint === field ? null : field;
  }


  // Manejo de la subida de imágenes de referencia
  onFileChange(event: any) {
    const element = event.target as HTMLInputElement;
    const files = element.files;
    if (!files) return;

    if (files.length > 3) {
      alert('Solo puedes subir un máximo de 3 imágenes.');
      element.value = '';
      this.selectedFiles = [];
      return;
    }

    const validFiles: File[] = [];
    const MAX_SIZE_MB = 5;
    const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024;
    const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

    for (let i = 0; i < files.length; i++) {
      const file = files[i];
      if (!ALLOWED_TYPES.includes(file.type)) {
        alert(`Archivo "${file.name}" no válido. Debes subir un JPG, PNG, GIF o WEBP.`);
        element.value = ''; this.selectedFiles = []; return;
      }
      if (file.size > MAX_SIZE_BYTES) {
        alert(`Archivo "${file.name}" demasiado grande. Máx. ${MAX_SIZE_MB}MB.`);
        element.value = ''; this.selectedFiles = []; return;
      }
      validFiles.push(file);
    }
    this.selectedFiles = validFiles;
  }

  //Limpiar el formulario con el botón de borrar
  resetForm() {
    this.bookingForm.reset({ colorMode: 'bw', needInvoice: false, acceptTerms: false, acceptDataProtection: false });
    this.selectedSlot = null;
    this.selectedDateStr = null;
    this.selectedDayIndex = null;
    this.weekDaysToDisplay = [];
    this.backendSlotsMap.clear();
    this.selectedFiles = [];
    const fileInput = document.getElementById('references') as HTMLInputElement;
    if (fileInput) fileInput.value = '';
  }

  //Resetear el formulario al usar el botón cancelar
  cancelForm() {
    this.resetForm();
    this.isFormOpen = false;
  }

  //Envío de la información del formulario
  onSubmit() {
    if (this.bookingForm.valid && this.selectedSlot && this.selectedDateStr && this.assignedWorkerId) {
      const raw = this.bookingForm.value;

      // Construcción del DTO para el backend
      const citaObjeto = {
        tipo: this.mapearServicio(raw.service),
        zona: this.mapearZona(raw.bodyZone),
        tamanio: this.mapearTamanio(raw.size),
        detalle: this.mapearDetalle(raw.detailLevel),
        coloracion: raw.colorMode === 'bw' ? 'NEGRO' : 'COLOR',
        estilo: this.mapearEstilo(raw.style),
        fecha: this.selectedDateStr,
        hora: this.selectedSlot + ":00", // El backend espera un formato HH:mm:ss
        comentarios: raw.comments,
        factura: raw.needInvoice ? 1 : 0,
        estatus: 'PENDIENTE',
        trabajador: { idTrabajador: this.assignedWorkerId },
        cliente: {
          nombre: raw.firstName,
          apellido1: raw.lastSurname,
          apellido2: raw.secondSurname,
          email: raw.email.toLowerCase(),
          telefono: raw.phone,
          documentoIdentificacion: raw.cif ? raw.cif : null,
        }
      };

      const formData = new FormData();
      formData.append('cita', JSON.stringify(citaObjeto));
      this.selectedFiles.forEach(file => formData.append('ficheros', file));

      this.appointmentService.createAppointment(formData).subscribe({
        next: (res) => {
          alert('¡Solicitud enviada! En breve recibirás un correo de confirmación.');
          this.resetForm();
          this.isFormOpen = false;
        },
        error: (err) => {
          console.error(err);
          alert('Se ha producido un error al enviar la solicitud.');
        }
      });
    } else {
      this.bookingForm.markAllAsTouched();
    }
  }

  // Validación de que el email y la repetición coinciden
  emailMatchValidator(form: AbstractControl): ValidationErrors | null {
    const e = form.get('email')?.value;
    const c = form.get('confirmEmail')?.value;
    return (e && c && e === c) ? null : { emailsDontMatch: true };
  }

  // Mapeado de algunos elementos
  private mapearZona(v: string): string { return this.MAPA_ZONAS[v] || 'BRAZO'; }
  private mapearServicio(v: string): string { return this.MAPA_SERVICIOS[v] || 'TATUAJE'; }
  private mapearTamanio(v: string): string { return this.MAPA_TAMANIOS[v] || 'MEDIANO'; }
  private mapearEstilo(v: string): string { return this.MAPA_ESTILOS[v] || 'REALISMO'; }
  private mapearDetalle(v: string): string { return this.MAPA_DETALLES[v] || 'MEDIO'; }
}