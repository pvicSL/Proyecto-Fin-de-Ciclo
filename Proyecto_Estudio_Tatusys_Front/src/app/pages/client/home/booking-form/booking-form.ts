import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { AppointmentService } from '../../../../core/services/appointment.service';

interface DaySlot {
  day: number;
  weekday: string;
  status: 'available' | 'none' | 'closed';
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

  isFormOpen: boolean = false;
  bookingForm: FormGroup;
  selectedDayIndex: number | null = null;
  selectedSlot: string | null = null;

  // NUEVA: Guardaremos aquí la fecha real (ej: "2025-12-30")
  selectedDateStr: string | null = null;

  // Empieza vacío. Se rellenará automáticamente al elegir servicio y tamaño.
  mockDays: DaySlot[] = [];

  //Variables de calendario
  currentViewDate: Date = new Date(); // La fecha base para calcular la semana visible
  weekDaysToDisplay: any[] = [];      // Array con los 7 días visuales (Lunes-Domingo)
  backendSlotsMap: Map<string, string[]> = new Map(); // Mapa para buscar rápido: "2026-12-30" -> ["10:00"]

  constructor(
    private fb: FormBuilder,
    private appointmentService: AppointmentService // Inyectamos tu servicio
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

    // PASO 3: Escuchar cambios para actualizar disponibilidad
    this.escucharCambiosParaDisponibilidad();
  }

  ngOnInit(): void {
    // No hacemos nada al iniciar. 
    // Esperamos a que el usuario rellene el formulario para buscar huecos.
  }

  // Mes en texto (ej: "DICIEMBRE")
  get currentMonthName(): string {
    return new Intl.DateTimeFormat('es-ES', { month: 'long', year: 'numeric' }).format(this.currentViewDate).toUpperCase();
  }

  // Lógica para detectar cuándo el usuario rellena lo necesario para saber la duración
  // 1. EL LISTENER (Sustituye al anterior)
  private escucharCambiosParaDisponibilidad() {
    this.bookingForm.valueChanges.subscribe(valores => {
      if (valores.service && valores.size && valores.detailLevel) {

        // ... (cálculo duración igual que antes) ...
        let duracionEstimada = 120; // Tu lógica de duración

        this.appointmentService.getAvailableSlots(duracionEstimada).subscribe({
          next: (respuestaBackend) => {
            // A) Guardamos los datos en un mapa para acceso rápido
            // respuestaBackend es { "2026-12-30": ["10:00"], ... }
            this.backendSlotsMap = new Map(Object.entries(respuestaBackend));

            // B) Reseteamos la vista a la fecha de hoy
            this.currentViewDate = new Date();

            // C) Generamos los 7 días visuales
            this.generateWeekGrid();
          }
        });
      }
    });
  }

  // --- 2. GENERADOR DE LA REJILLA (EL CEREBRO) ---
  generateWeekGrid() {
    this.weekDaysToDisplay = [];
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Borramos hora para comparar solo fechas

    // 1. Encontrar el Lunes de la semana 'currentViewDate'
    const startOfWeek = this.getMonday(this.currentViewDate);

    // 2. Generar los 7 días consecutivos
    for (let i = 0; i < 7; i++) {
      const iterDate = new Date(startOfWeek);
      iterDate.setDate(startOfWeek.getDate() + i); // Lunes + 0, Lunes + 1...

      const iterDateString = iterDate.toISOString().split('T')[0]; // "2025-12-29"

      // 3. Determinar estado del día
      const isWeekend = (iterDate.getDay() === 0 || iterDate.getDay() === 6); // 0=Dom, 6=Sab
      const isPast = iterDate < today; // Si es anterior a hoy
      const isToday = iterDate.getTime() === today.getTime();

      // Buscamos si el Back nos dio huecos para este día exacto
      const slots = this.backendSlotsMap.get(iterDateString) || [];
      const hasSlots = slots.length > 0;

      // Estado final: 'disabled' (pasado), 'weekend' (finde), 'available' (con huecos), 'empty' (futuro sin huecos)
      let status = 'empty';
      if (isPast) status = 'disabled';
      else if (isToday) status = 'disabled'; // Req 1: Hoy no disponible, solo a partir de mañana
      else if (isWeekend) status = 'weekend'; // Req 5
      else if (hasSlots) status = 'available';

      this.weekDaysToDisplay.push({
        dateObj: iterDate,
        dateStr: iterDateString,
        dayNumber: iterDate.getDate(),
        weekdayLetter: this.getWeekdayLetter(iterDate), // L, M, X...
        status: status,
        slots: slots
      });
    }
  }

  // --- 3. NAVEGACIÓN ---
  prevMonth() {
    const newDate = new Date(this.currentViewDate);
    newDate.setMonth(newDate.getMonth() - 1);

    // Req 4: No permitir ir a meses anteriores al actual
    const today = new Date();
    if (newDate.getMonth() < today.getMonth() && newDate.getFullYear() <= today.getFullYear()) {
      return; // Bloqueado
    }
    this.currentViewDate = newDate;
    this.generateWeekGrid();
  }

  nextMonth() {
    const newDate = new Date(this.currentViewDate);
    newDate.setMonth(newDate.getMonth() + 1);
    this.currentViewDate = newDate;
    this.generateWeekGrid();
  }

  prevWeek() {
    const today = new Date();
    const prevWeekDate = new Date(this.currentViewDate);
    prevWeekDate.setDate(prevWeekDate.getDate() - 7);

    // Opcional: Bloquear ir semanas muy atrás del día actual
    // if (this.getMonday(prevWeekDate) < this.getMonday(today)) return;

    this.currentViewDate = prevWeekDate;
    this.generateWeekGrid();
  }

  nextWeek() {
    const nextWeekDate = new Date(this.currentViewDate);
    nextWeekDate.setDate(nextWeekDate.getDate() + 7);
    this.currentViewDate = nextWeekDate;
    this.generateWeekGrid();
  }

  // --- UTILIDADES ---
  // Obtiene el objeto fecha del Lunes de la semana dada
  private getMonday(d: Date): Date {
    const date = new Date(d);
    const day = date.getDay();
    const diff = date.getDate() - day + (day === 0 ? -6 : 1); // ajustar cuando es domingo
    return new Date(date.setDate(diff));
  }

  private getWeekdayLetter(d: Date): string {
    const letras = ['D', 'L', 'M', 'X', 'J', 'V', 'S'];
    return letras[d.getDay()];
  }

  // Método al seleccionar un día
  selectDay(day: any) {
    if (day.status !== 'available') return;

    // Marcamos visualmente
    this.selectedDayIndex = this.weekDaysToDisplay.indexOf(day);

    // GUARDAMOS EL DATO REAL PARA EL ENVÍO
    this.selectedDateStr = day.dateStr; // Esto guarda "2025-12-30"

    this.selectedSlot = null;
  }

  // 2. LA FUNCIÓN DE TRANSFORMACIÓN (NUEVA)
  // Convierte { "2026-12-20": ["10:00"] }  --->  [{ day: 20, weekday: 'D'... }]
  private transformarRespuestaACalendario(dataBackend: any): DaySlot[] {
    const diasGenerados: DaySlot[] = [];
    const letrasDias = ['D', 'L', 'M', 'X', 'J', 'V', 'S']; // 0=Domingo, 1=Lunes...

    // Recorremos las claves del objeto (las fechas tipo "2026-12-20")
    for (const fechaString in dataBackend) {
      if (dataBackend.hasOwnProperty(fechaString)) {

        const huecos = dataBackend[fechaString]; // Es la lista ["10:00", "10:30"]
        const fechaObj = new Date(fechaString);  // Convertimos string a objeto Date JS

        diasGenerados.push({
          day: fechaObj.getDate(), // Sacamos el número (ej: 20)
          weekday: letrasDias[fechaObj.getDay()], // Sacamos la letra según el día de la semana
          status: huecos.length > 0 ? 'available' : 'none', // Si tiene huecos es 'available'
          slots: huecos // Asignamos las horas reales
        });
      }
    }

    // Ordenamos por día (por si acaso el mapa viene desordenado)
    diasGenerados.sort((a, b) => a.day - b.day);

    return diasGenerados;
  }

  //Animacion suave para abrir el formulario
  toggleForm() {
    this.isFormOpen = !this.isFormOpen;
    if (this.isFormOpen) {
      setTimeout(() => {
        const element = document.getElementById('form-anchor');
        if (element) element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }, 100);
    }
  }


  selectSlot(time: string) {
    this.selectedSlot = time;
  }

  resetForm() {
    // 1. Limpiamos los campos del formulario (Angular)
    this.bookingForm.reset({
      colorMode: 'bw',
      needInvoice: false,
      acceptTerms: false
    });

    // 2. Limpiamos las variables de SELECCIÓN
    this.selectedSlot = null;
    this.selectedDateStr = null;
    this.selectedDayIndex = null;

    // 3. Limpiamos los DATOS DEL CALENDARIO (Esto hará que desaparezca)
    // Al vaciar este array, el *ngIf="weekDaysToDisplay.length > 0" del HTML se vuelve falso
    // y oculta todo el bloque del calendario.
    this.weekDaysToDisplay = [];

    // 4. Limpiamos la caché del backend (opcional, pero recomendado)
    this.backendSlotsMap.clear();

    console.log("Formulario y calendario reseteados completamente.");
  }

  onSubmit() {
    // 1. Verificamos que el formulario sea válido y se haya elegido hora
    if (this.bookingForm.valid && this.selectedSlot) {
      const raw = this.bookingForm.value;

      // 2. PREPARACIÓN DE DATOS (Esto lo tenías bien)
      const citaParaEnviar = {
        tipo: this.mapearServicio(raw.service),
        zona: this.mapearZona(raw.bodyZone),
        tamanio: this.mapearTamanio(raw.size),
        detalle: raw.detailLevel.toUpperCase(),
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
          email: raw.email,
          telefono: raw.phone,
          documentoIdentificacion: raw.cif
        }
      };

      console.log('ENVIANDO CITA A BD:', citaParaEnviar);

      // 3. ENVÍO REAL
      // Llamamos al servicio y nos suscribimos a la respuesta
      this.appointmentService.createAppointment(citaParaEnviar).subscribe({
        next: (respuesta) => {
          // Si entra aquí, es que Spring Boot ha guardado la cita (HTTP 200)
          console.log('Cita guardada:', respuesta);
          alert('¡Solicitud enviada y guardada en base de datos!');

          this.resetForm();
          this.isFormOpen = false;
        },
        error: (error) => {
          // Si entra aquí, algo falló (HTTP 400, 500, etc.)
          console.error('Error al guardar:', error);
          alert('Error al conectar con el servidor. Revisa la consola.');
        }
      });

    } else {
      // Si el formulario no es válido, marcamos los errores en rojo
      this.bookingForm.markAllAsTouched();
    }
  }

  // Función para corregir las erratas del Backend en las zonas
  private mapearZona(valor: string): string {
    if (!valor) return 'BRAZO';

    const mayusculas = valor.toUpperCase();

    const mapa: any = {
      'CERVICAL': 'CERVIAL',       // Mapeamos al error del backend
      'PANTORRILLA': 'PANTORILLA', // Mapeamos al error del backend (falta una R)
      'TÓRAX': 'TÓRAX',            // Aseguramos tildes
      'TORAX': 'TÓRAX'             // Por si viene sin tilde
    };

    // Si está en el mapa, devolvemos el valor "corregido", si no, el valor en mayúsculas
    return mapa[mayusculas] || mayusculas;
  }

  // --- FUNCIÓN DE TRADUCCIÓN PARA ESTILOS ---
  private mapearEstilo(valor: string): string {
    if (!valor) return 'REALISMO'; // Valor por defecto si viene vacío

    // 1. Convertimos a mayúsculas: "Japonés" -> "JAPONÉS"
    const mayusculas = valor.toUpperCase();

    // 2. Mapa de traducción manual
    const mapa: any = {
      'JAPONÉS': 'JAPONES',       // AQUÍ QUITAMOS LA TILDE
      'JAPONES': 'JAPONES',
      'LETTERING': 'LETERING',    // Corregimos la doble T para que coincida con tu BBDD
      'REALISMO': 'REALISMO',
      'TRADICIONAL': 'TRADICIONAL',
      'FINELINE': 'FINELINE',
      'BLACK AND GREY': 'BLACKANDGREY', // Quitamos espacios
      'ANIME': 'ANIME'
    };

    // 3. Devolvemos el valor traducido o el original limpio si no está en la lista
    return mapa[mayusculas] || mayusculas.replace(/\s/g, '');
  }

  // Función auxiliar para traducir los tamaños de HTML a Java
  private mapearTamanio(valor: string): string {
    const mapa: any = {
      'mini': 'MINI',
      'small': 'PEQUEÑO',      // Traducimos small -> PEQUEÑO
      'medium': 'MEDIANO',     // Traducimos medium -> MEDIANO
      'large': 'GRANDE',       // Traducimos large -> GRANDE
      'xtralarge': 'MUY_GRANDE' // Traducimos xtralarge -> MUY_GRANDE
    };
    return mapa[valor] || 'MEDIANO'; // Valor por defecto si falla
  }

  private mapearServicio(valor: string): string {
    const mapeo: any = {
      'tattoo-new': 'TATUAJE',
      'tattoo-retouch': 'ELIMINACION',
      'cover-up': 'COVER',
      'design-only': 'RETOQUE'
    };
    return mapeo[valor] || 'TATUAJE';
  }

  emailMatchValidator(form: AbstractControl): ValidationErrors | null {
    const email = form.get('email')?.value;
    const confirm = form.get('confirmEmail')?.value;
    if (!email || !confirm) return null;
    return email === confirm ? null : { emailsDontMatch: true };
  }

  onFileChange(event: any) {
    const files = event.target.files;
    if (files.length > 3) {
      alert('Máximo de 3 imágenes.');
      event.target.value = '';
    }
  }

  // Variable para saber si estamos editando (añádela arriba con las propiedades)
  currentAppointmentId: number | null = null;

  // Llama a esta función cuando hagas click en "Editar" en una cita
  cargarDatosParaEditar(cita: any) {
    this.isFormOpen = true;
    this.currentAppointmentId = cita.idCita; // Guardamos el ID para luego hacer UPDATE en vez de CREATE

    // 1. Rellenamos el formulario con los datos de la BBDD
    // (Tendrás que ajustar los nombres de campos para que coincidan)
    this.bookingForm.patchValue({
      firstName: cita.cliente.nombre,
      lastSurname: cita.cliente.apellido1,
      email: cita.cliente.email,
      phone: cita.cliente.telefono,
      comments: cita.comentarios,
      // OJO: Aquí necesitas transformar los valores de BBDD (MAYÚSCULAS) a los del Formulario (minúsculas)
      // Por ejemplo: si cita.tamanio es 'PEQUEÑO', el form espera 'small'
      size: this.mapearTamanioInverso(cita.tamanio),
      service: this.mapearServicioInverso(cita.tipo),
      detailLevel: cita.detalle ? cita.detalle.toLowerCase() : '',
      // ... resto de campos ...
    });

    // 2. AQUÍ PEGAS EL CÓDIGO QUE TE PASÉ
    // Forzamos la búsqueda de huecos manualmente para que aparezca el calendario
    const duracion = this.calcularDuracionSegunTamano(cita.tamanio);

    this.appointmentService.getAvailableSlots(duracion).subscribe(data => {
      this.mockDays = this.transformarRespuestaACalendario(data);

      // Opcional: Si quieres pre-seleccionar la hora que ya tenía la cita
      // this.selectedSlot = cita.hora.substring(0, 5); 
    });
  }

  // Calcula minutos basándose en lo que viene de la BBDD
  private calcularDuracionSegunTamano(tamanioBBDD: string): number {
    if (!tamanioBBDD) return 120; // Por defecto

    switch (tamanioBBDD) {
      case 'MINI': return 60;
      case 'PEQUEÑO': return 90;
      case 'MEDIANO': return 120;
      case 'GRANDE': return 180;
      case 'MUY_GRANDE': return 240;
      default: return 120;
    }
  }

  // Convierte de BBDD (PEQUEÑO) a Formulario (small)
  private mapearTamanioInverso(valor: string): string {
    const mapa: any = {
      'MINI': 'mini',
      'PEQUEÑO': 'small',
      'MEDIANO': 'medium',
      'GRANDE': 'large',
      'MUY_GRANDE': 'xtralarge'
    };
    return mapa[valor] || 'medium';
  }

  // Convierte de BBDD (TATUAJE) a Formulario (tattoo-new)
  private mapearServicioInverso(valor: string): string {
    const mapa: any = {
      'TATUAJE': 'tattoo-new',
      'ELIMINACION': 'tattoo-retouch',
      'COVER': 'cover-up',
      'RETOQUE': 'design-only'
    };
    return mapa[valor] || 'tattoo-new';
  }


  // SECCION PARA MENSAJES EMERGENTES DE AYUDA EN FORMULARIO
  // Variable para controlar qué ayuda se muestra (ej: 'size', 'style', etc.)
  activeHint: string | null = null;

  // Función para abrir/cerrar la ayuda al hacer click en el icono
  toggleHint(field: string) {
    if (this.activeHint === field) {
      this.activeHint = null; // Si ya estaba abierto, lo cerramos
    } else {
      this.activeHint = field; // Abrimos el nuevo
    }
  }
}