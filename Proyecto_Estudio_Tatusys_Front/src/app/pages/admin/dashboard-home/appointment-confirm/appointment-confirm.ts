import { ChangeDetectorRef, Component,} from '@angular/core';
import { AppointmentModify } from '../../../../shared/components/admin/appointment-modify/appointment-modify';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PricesService } from '../../../../core/services/prices.service';
import { UploadService } from '../../../../core/services/upload.service';


@Component({
  selector: 'app-appointment-confirm',
  imports: [AppointmentModify],
  templateUrl: './appointment-confirm.html',
  styleUrl: './appointment-confirm.css',
})

export class AppointmentConfirm extends AppointmentModify{

  constructor(
    // Debes inyectarlo aquí también
    appointmentService: AppointmentService,
    route: ActivatedRoute,
    priceService: PricesService,
    uploadService: UploadService,
    cdr: ChangeDetectorRef,
    private router: Router 
  ) {
    super(appointmentService, route, priceService, uploadService, cdr); // Se lo envías al constructor del padre
  }
  
  override guardarPresupuesto() {
  if (this.cita && this.cita.idCita) {
    // Asegúrate de que los comentarios del textarea se guarden en el campo correcto
    console.log("Datos a enviar:", this.cita);

    this.appointmentService.updateAppointmentDetails(this.cita.idCita, this.cita)
      .subscribe({
        next: (res) => {
          alert('¡Presupuesto actualizado correctamente!');
        },
        error: (err) => {
          console.error("Error del servidor:", err);
          alert('Error al guardar: ' + (err.error?.message || 'Revisa la consola'));
        }
      });
  }
}

finalizarCita() {
    if (!this.citaDTO || !this.citaDTO.idCita) return;

    this.appointmentService.finalizarTrabajo(this.cita.idCita)
      .subscribe({
        next: (res) => {
          console.log("Cita finalizada con éxito");

          // 3. Lógica de navegación basada en el atributo 'factura'
          // Asumo que 'factura' viene dentro del objeto 'cita' o en la respuesta 'res'
          const estadoFactura = this.citaDTO.estadoFactura; 

          if (estadoFactura === 'NO_REQUIERE') {
            this.router.navigate(['/admin/citas/generarFactura']);
          } 
          else if (estadoFactura === 'PENDIENTE') {
            this.router.navigate(['/admin/citas/facturaGenerada']);
          }
          else {
            // Caso por defecto si el estado es distinto o ya está PAGADA
            alert('Trabajo finalizado sin cambios en la factura.');
          }
        },
        error: (err) => {
          console.error("Error al finalizar:", err);
          alert('Hubo un error al finalizar el trabajo.');
        }
      });
  }

}
