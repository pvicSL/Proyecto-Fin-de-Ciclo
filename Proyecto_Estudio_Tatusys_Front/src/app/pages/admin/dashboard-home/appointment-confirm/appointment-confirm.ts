import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Importante para pipes y directivas
import { FormsModule } from '@angular/forms';   // Necesario para [(ngModel)]
import { PresupuestoService } from '../../../../core/services/presupuesto.service'; // Ajusta la ruta a tu servicio
import { AppointmentService } from '../../../../core/services/appointment.service';
import { ActivatedRoute } from '@angular/router';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';


@Component({
  selector: 'app-appointment-confirm',
  imports: [CommonModule, FormsModule, FormatoHorasPipe],
  templateUrl: './appointment-confirm.html',
  styleUrl: './appointment-confirm.css',
})
export class AppointmentConfirm implements OnInit {

  
  cita!: AppointmentAdminDTO;

  constructor(
    private presupuestoService: PresupuestoService,
    private appointmentService: AppointmentService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    const id = this.route.snapshot.params['id'];
    // 1. Primero obtenemos los datos del cliente/cita
    this.appointmentService.getDetalleCita(id).subscribe(datosCita => {
      
      // 2. Luego obtenemos el presupuesto
      this.presupuestoService.obtenerPresupuestoPorCita(id).subscribe(datosPresupuesto => {
        
        // 3. FUSIONAMOS AMBOS: 
        // Creamos un único objeto que tenga las propiedades de la cita y del presupuesto
        this.cita = {
          ...datosCita,        // Trae nombre, apellido, email, tipo, zona...
          ...datosPresupuesto  // Trae idPresupuesto, precioExtra, precioBase, etc.
        };
        
        console.log('Datos unificados:', this.cita);
      });
    });
  }

  guardarPresupuesto() {
    if (this.cita && this.cita.idServicio) {
      this.presupuestoService.actualizarPresupuestoConExtra(this.cita.idServicio, this.cita)
        .subscribe({
          next: (res) => {
            // Reemplazamos los datos con la respuesta del servidor (que ya trae IVA y Total nuevos)
            this.cita = res; 
            alert('¡Presupuesto actualizado correctamente!');
            //  LLAMAMOS A TU FUNCIÓN DE CERRAR
            this.cerrarDetalle();
          },
          error: (err) => {
            console.error('Error al actualizar:', err);
            alert('No se pudo guardar el presupuesto.');
          }
        });
    }
  }

  cerrarDetalle() {
    // Lógica para cerrar o volver atrás
    window.history.back();
  }
}
