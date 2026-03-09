import { Component, signal, computed, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-invoice-question',
  standalone: true, // Angular moderno es standalone por defecto
  imports: [FormsModule],
  templateUrl: './invoice-question.html',
  styleUrl: './invoice-question.css',
})
export class InvoiceQuestion implements OnInit {
  // Inyecciones modernas con inject()
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private appointmentService = inject(AppointmentService);

  // Signals para el estado
  citaId = signal<number>(0);
  loading = signal<boolean>(false);
  estadoFactura = signal<'PENDIENTE' | 'NO_REQUIERE' | string>('');
  
  dni = signal<string>('');
  direccion = signal<string>('');

  // Computed signal para la validación (se actualiza sola)
  isFormInvalid = computed(() => {
    if (this.estadoFactura() === 'PENDIENTE') {
      return !this.dni().trim() || !this.direccion().trim() || this.loading();
    }
    return this.loading();
  });

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.citaId.set(id);

    // Simulamos la carga del estado desde el servicio
    this.appointmentService.getAppointment(id).subscribe(cita => {
      this.estadoFactura.set(cita.estadoFactura);
    });
  }

  confirmarGenerarFactura() {
    this.loading.set(true);
    this.appointmentService.generarFacturaManual(this.citaId(), this.dni(), this.direccion())
      .subscribe({
        next: () => {
          this.router.navigate(['/admin/facturas']);
        },
        error: () => this.loading.set(false)
      });
  }

  irAlListado() {
    this.router.navigate(['/admin/citas']);
  }
}