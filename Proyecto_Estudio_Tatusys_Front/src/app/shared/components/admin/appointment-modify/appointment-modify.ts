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

  /*

  actualizarCalculos() {
    if (!this.cita || !this.precio) return;

    // 1. Precio Base del servicio
    const base = this.precio.precioBase || 0;

    // 2. Precio por Tamaño (Normalizamos la key: 'PEQUEÑO' -> 'pequeno')
    const keyTamanio = this.cita.tamanio;
    const precioTamanio = (this.precio.precioTamanio as any)[keyTamanio] || 0;

    // 3. Precio por Detalle
    const keyDetalle = this.cita.detalle;
    const precioDetalle = (this.precio.precioDetalle as any)[keyDetalle] || 0;
    
    const keyEstilo = this.cita.estilo;
    const precioEstilo = (this.precio.precioEstilo as any)[keyEstilo] || 0;

    const keyColoracion = this.cita.coloracion;
    const precioColoracion = (this.precio.precioColoracion as any)[keyColoracion] || 0;

    const keyTipo = this.cita.tipo;
    const precioTipo = (this.precio.precioTipo as any)[keyTipo] || 0;

    const keyZona = this.cita.zona;
    const precioZona = (this.precio.precioZona as any)[keyZona] || 0;


    // 5. Totales
    this.cita.precioBase = base + precioTamanio + precioDetalle + precioEstilo + precioColoracion + precioTipo + precioZona;
    this.cita.iva = this.cita.precioBase * 0.21;
    this.cita.precioFinal = this.cita.precioBase + this.cita.iva;
  }
    */

  // Si pulsa la 'X' o cerrar sin guardar
  cerrarDetalle() {
    // Restauramos el original antes de volver atrás para limpiar la memoria
    this.cita = JSON.parse(JSON.stringify(this.citaOriginal));
    window.history.back();
  }

  guardarPresupuesto() {
  if (this.cita && this.cita.idCita) {
    // Asegúrate de que los comentarios del textarea se guarden en el campo correcto
    console.log("Datos a enviar:", this.cita);

    this.appointmentService.updateAppointmentDetails(this.cita.idCita, this.cita)
      .subscribe({
        next: (res) => {
          alert('¡Presupuesto actualizado correctamente!');
          window.history.back();
        },
        error: (err) => {
          console.error("Error del servidor:", err);
          alert('Error al guardar: ' + (err.error?.message || 'Revisa la consola'));
        }
      });
  }
}


  
} 
