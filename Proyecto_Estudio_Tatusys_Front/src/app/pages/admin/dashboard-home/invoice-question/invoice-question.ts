import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-invoice-question',
  imports: [CommonModule, FormsModule],
  templateUrl: './invoice-question.html',
  styleUrl: './invoice-question.css',
})
export class InvoiceQuestion {
citaId!: number;
  loading: boolean = false;
  
  // Campos opcionales para la factura
  dni: string = '';
  direccion: string = '';

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private appointmentService: AppointmentService
  ) {}
  

  ngOnInit() {
    // Obtenemos el ID de la cita de la URL (ej: /generar-factura/15)
    this.citaId = Number(this.route.snapshot.paramMap.get('id'));
  }

  confirmarGenerarFactura() {
    this.loading = true;
    this.appointmentService.generarFacturaManual(this.citaId, this.dni, this.direccion).subscribe({
      next: () => {
        alert('Factura generada con éxito. La encontrarás en el listado de facturas.');
        this.router.navigate(['/admin/facturas']);
      },
      error: (err) => {
        console.error('Error al generar factura', err);
        this.loading = false;
      }
    });
  }

  irAlListado() {
    this.router.navigate(['/admin/citas']); 
  }
}
