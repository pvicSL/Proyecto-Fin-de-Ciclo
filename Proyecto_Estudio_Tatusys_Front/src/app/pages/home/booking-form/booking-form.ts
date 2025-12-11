import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Necesario para *ngIf y *ngFor
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';

// Definimos cómo es un día en nuestro calendario falso
interface DaySlot {
  day: number;
  weekday: string;
  status: 'available' | 'none' | 'closed';
  slots: string[];
}

@Component({
  selector: 'app-booking-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule], // ¡IMPORTANTE: Importar módulos aquí!
  templateUrl: './booking-form.html',
  styleUrl: './booking-form.css'
})
export class BookingFormComponent implements OnInit {

  // Variables de control visual
  isFormOpen: boolean = false; // Controla si se ve el formulario o el botón
  bookingForm: FormGroup;      // Aquí se guarda todo el formulario

  // Variables para el calendario
  selectedDayIndex: number | null = null;
  selectedSlot: string | null = null;

  // Datos MOCKUP (Simulación de lo que devolvería Spring Boot)
  mockDays: DaySlot[] = [
    { day: 18, weekday: 'L', status: 'available', slots: ['09:00', '12:00', '16:00'] },
    { day: 19, weekday: 'M', status: 'available', slots: ['13:00', '16:00', '19:00'] },
    { day: 20, weekday: 'X', status: 'none', slots: [] },
    { day: 21, weekday: 'J', status: 'available', slots: ['10:00', '18:00'] },
    { day: 22, weekday: 'V', status: 'closed', slots: [] },
    { day: 23, weekday: 'S', status: 'available', slots: ['11:00'] },
    { day: 24, weekday: 'D', status: 'none', slots: [] }
  ];

  constructor(private fb: FormBuilder) {
    // Inicializamos el formulario con sus campos y validaciones
    this.bookingForm = this.fb.group({
      firstName: ['', Validators.required],
      lastSurname: ['', Validators.required],
      secondSurname: [''], // Opcional
      email: ['', [Validators.required, Validators.email]],
      confirmEmail: ['', Validators.required],
      phone: ['', Validators.required],
      needInvoice: [false],
      // Campos condicionales de factura
      cif: [''],
      billingAddress: [''],
      // Datos del servicio
      service: ['', Validators.required],
      bodyZone: ['', Validators.required],
      size: ['', Validators.required],
      style: ['', Validators.required],
      detailLevel: ['', Validators.required], // "Detalle" en tu HTML
      colorMode: ['bw', Validators.required],
      comments: [''],
      references: [''],
      acceptTerms: [false, Validators.requiredTrue] // Debe ser true obligatoriamente
    }, { validators: this.emailMatchValidator }); // Añadimos validación personalizada grupal
  }

  ngOnInit(): void {
    // Al iniciar, pre-seleccionamos el primer día disponible para que quede bonito
    this.selectedDayIndex = this.mockDays.findIndex(d => d.status === 'available');
  }

  // --- MÉTODOS DE ACCIÓN ---

  toggleForm() {
    this.isFormOpen = !this.isFormOpen;
    // Si se abre, hacemos scroll suave hacia el formulario
    if (this.isFormOpen) {
      setTimeout(() => {
        const element = document.getElementById('form-anchor');
        if (element) element.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }, 100);
    }
  }

  selectDay(index: number, day: DaySlot) {
    if (day.status !== 'available') return; // Si está cerrado, no hace nada
    this.selectedDayIndex = index;
    this.selectedSlot = null; // Reseteamos la hora porque hemos cambiado de día
  }

  selectSlot(time: string) {
    this.selectedSlot = time;
  }

  resetForm() {
    this.bookingForm.reset({ colorMode: 'bw', needInvoice: false, acceptTerms: false });
    this.selectedSlot = null;
    this.isFormOpen = false;
  }

  onSubmit() {
    // Si el formulario es válido y hay hora seleccionada
    if (this.bookingForm.valid && this.selectedSlot) {

      // Preparamos los datos para enviarlos (en el futuro a Spring Boot)
      const formData = {
        ...this.bookingForm.value,
        appointmentDate: `2026-XX-${this.mockDays[this.selectedDayIndex!].day}`,
        appointmentTime: this.selectedSlot
      };

      console.log('ENVIANDO AL BACKEND:', formData);
      alert('¡Solicitud enviada correctamente! (Simulación)');
      this.resetForm();

    } else {
      // Si hay errores, marcamos todo como "tocado" para que salgan en rojo
      this.bookingForm.markAllAsTouched();
    }
  }

  // --- VALIDADOR PERSONALIZADO ---
  // Comprueba que email y confirmEmail sean iguales
  emailMatchValidator(form: AbstractControl): ValidationErrors | null {
    const email = form.get('email')?.value;
    const confirm = form.get('confirmEmail')?.value;
    // Si alguno está vacío no validamos aún. Si son distintos, devolvemos error.
    if (!email || !confirm) return null;
    return email === confirm ? null : { emailsDontMatch: true };
  }

  // Método simple para controlar la subida de archivos
  onFileChange(event: any) {
    const files = event.target.files;
    if (files.length > 3) {
      alert('Máximo de 3 imágenes.');
      event.target.value = ''; // Limpiar el input
    }
    // Aquí podrías guardar los archivos en una variable para enviarlos luego
  }
}
