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

  // Empezamos con el mockup, pero esto se actualizará dinámicamente
  mockDays: DaySlot[] = [
    { day: 18, weekday: 'L', status: 'available', slots: ['09:00', '12:00', '16:00'] },
    { day: 19, weekday: 'M', status: 'available', slots: ['13:00', '16:00', '19:00'] },
    { day: 20, weekday: 'X', status: 'none', slots: [] },
    { day: 21, weekday: 'J', status: 'available', slots: ['10:00', '18:00'] },
    { day: 22, weekday: 'V', status: 'closed', slots: [] },
    { day: 23, weekday: 'S', status: 'available', slots: ['11:00'] },
    { day: 24, weekday: 'D', status: 'none', slots: [] }
  ];

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
    this.selectedDayIndex = this.mockDays.findIndex(d => d.status === 'available');
  }

  // Lógica para detectar cuándo el usuario rellena lo necesario para saber la duración
  private escucharCambiosParaDisponibilidad() {
    this.bookingForm.valueChanges.subscribe(valores => {
      // Si tenemos los campos que afectan a la duración, pedimos huecos
      if (valores.service && valores.size && valores.detailLevel) {
        console.log("Detectados cambios en parámetros del servicio. Buscando huecos...");

        // Aquí llamaríamos al back. De momento usamos tu mockup del service:
        this.appointmentService.getAvailableSlots(120).subscribe(huecos => {
          // Aquí en el futuro procesarás los huecos reales del back
          console.log("Huecos recibidos del servidor:", huecos);
        });
      }
    });
  }

  toggleForm() {
    this.isFormOpen = !this.isFormOpen;
    if (this.isFormOpen) {
      setTimeout(() => {
        const element = document.getElementById('form-anchor');
        if (element) element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }, 100);
    }
  }

  selectDay(index: number, day: DaySlot) {
    if (day.status !== 'available') return;
    this.selectedDayIndex = index;
    this.selectedSlot = null;
  }

  selectSlot(time: string) {
    this.selectedSlot = time;
  }

  resetForm() {
    this.bookingForm.reset({ colorMode: 'bw', needInvoice: false, acceptTerms: false });
    this.selectedSlot = null;
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
        // OJO: Ajusta esto cuando tengas fechas reales. Ahora usa el mock.
        fecha: `2026-12-${this.mockDays[this.selectedDayIndex!].day}`,
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

      // 3. ENVÍO REAL (AQUÍ ESTÁ EL CAMBIO)
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
}