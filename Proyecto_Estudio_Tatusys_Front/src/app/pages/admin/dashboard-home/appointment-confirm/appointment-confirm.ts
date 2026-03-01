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
    if (!this.cita || !this.cita.idCita) return; // Asegúrate de usar el objeto correcto (cita o citaDTO)

    this.appointmentService.finalizarTrabajo(this.cita.idCita)
      .subscribe({
        next: (res) => {
          console.log("Cita finalizada con éxito");

          // Usa el ID real de la cita para la ruta
          const idActual = this.cita.idCita; 

          // IMPORTANTE: No pongas ':id', pon la variable con el número
          if (this.citaDTO.estadoFactura === 'NO_REQUIERE') {
            this.router.navigate(['/admin/citas/generarFactura', idActual]); 
          } 
          else if (this.citaDTO.estadoFactura === 'PENDIENTE') {
            this.router.navigate(['/admin/citas/facturaGenerada']);
          }
        },
        error: (err) => {
          console.error("Error al finalizar:", err);
        }
      });
}

}
