import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AppointmentService } from '../../../../core/services/appointment.service';
import { ActivatedRoute } from '@angular/router';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';
import { PricesService } from '../../../../core/services/prices.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';
import { PricesAdminDTO } from '../../../../core/models/prices-admin.model';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-appointment-modify',
  imports: [CommonModule, FormsModule, FormatoHorasPipe],
  templateUrl: './appointment-modify.html',
  styleUrls: ['./appointment-modify.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppointmentModify implements OnInit {

  cita!: AppointmentAdminDTO;
  citaDTO!: AppointmentDTO;
  precio!: PricesAdminDTO;
  citaOriginal!: AppointmentAdminDTO; // <-- Importante: El respaldo


  constructor(
    private appointmentService: AppointmentService,
    private route: ActivatedRoute,
    private priceService: PricesService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    
    forkJoin({
      admin: this.appointmentService.getAppointmentDetails(id),
      precios: this.priceService.getPrices(id),
      base: this.appointmentService.getAppointment(id)
    }).subscribe({
      next: (respuestas) => {
        // 1. Extraemos el objeto (por si viene como array)
        const datosAdmin = Array.isArray(respuestas.admin) ? respuestas.admin[0] : respuestas.admin;
        
        // 2. Asignamos a la vista
        this.cita = datosAdmin;
        this.precio = respuestas.precios;
        this.citaDTO = respuestas.base;

        // 3. CREAMOS LA COPIA DE SEGURIDAD
        this.citaOriginal = JSON.parse(JSON.stringify(datosAdmin));
      },
      error: (err) => console.error('Error al cargar:', err)
    });
  }

  // Si pulsa la 'X' o cerrar sin guardar
  cerrarDetalle() {
    // Restauramos el original antes de volver atrás para limpiar la memoria
    this.cita = JSON.parse(JSON.stringify(this.citaOriginal));
    window.history.back();
  }

  guardarPresupuesto(){
    if (this.cita && this.cita.idCita) {
      this.appointmentService.updateAppointmentDetails(this.cita.idCita, this.cita)
        .subscribe({
          next: (res) => {
            // Actualizamos el respaldo porque ahora la base de datos ya tiene estos datos
            this.citaOriginal = JSON.parse(JSON.stringify(this.cita));
            alert('¡Presupuesto actualizado correctamente!');
            window.history.back();
          },
          error: (err) => alert('No se pudo guardar.')
        });
    }
  }

  recalcularTodo(): void {
  this.appointmentService.simularPresupuesto(this.cita).subscribe({
    next: (res) => {
      // 1. Actualizamos los totales en el objeto 'cita'
      this.cita.precioBase = res.precioBase;
      this.cita.iva = res.iva;
      this.cita.precioFinal = res.precioFinal;

      // 2. IMPORTANTE: Actualizamos el objeto 'precio' 
      // para que los numeritos de los selects cambien
      this.precio = {
        ...this.precio, // mantenemos lo que no cambie
        precioBase: res.precioBase,
        precioZona: res.precioZona,
        precioTamanio: res.precioTamanio,
        precioDetalle: res.precioDetalle,
        precioColoracion: res.precioColoracion,
        precioEstilo: res.precioEstilo,
        precioTipo: res.precioTipo
      };
      
      console.log('Vista refrescada con nuevos precios');
    }
  });
}
  
} 
