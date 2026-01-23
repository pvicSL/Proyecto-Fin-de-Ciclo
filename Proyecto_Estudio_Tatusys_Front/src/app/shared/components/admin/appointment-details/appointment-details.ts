import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { ActivatedRoute } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';
import { PricesService } from '../../../../core/services/prices.service';
import { PricesAdminDTO } from '../../../../core/models/prices-admin.model';
import { AppointmentDTO } from '../../../../core/models/appointment.model';
import { forkJoin } from 'rxjs';
import { UploadService } from '../../../../core/services/upload.service';

@Component({
  selector: 'app-appointment-details',
  imports: [CommonModule, FormatoHorasPipe],
  templateUrl: './appointment-details.html',
  styleUrls: ['./appointment-details.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppointmentDetails implements OnInit {
  cita!: AppointmentAdminDTO;
  precio!: PricesAdminDTO;
  citaDTO!: AppointmentDTO


  constructor(
    private appointmentService: AppointmentService, 
    private route: ActivatedRoute,
    private location: Location,
    private priceService: PricesService,
    private uploadService: UploadService,
  ) {}

  ngOnInit(): void {
  const id = this.route.snapshot.params['id'];
  console.log('ID detectado en la URL:', id); // Primer punto de control

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

        // Transformamos los nombres de archivo en URLs completas para el HTML
        if (this.cita.imagenRef1) this.cita.imagenRef1 = this.uploadService.getImagenUrl(this.cita.imagenRef1);
        if (this.cita.imagenRef2) this.cita.imagenRef2 = this.uploadService.getImagenUrl(this.cita.imagenRef2);
        if (this.cita.imagenRef3) this.cita.imagenRef3 = this.uploadService.getImagenUrl(this.cita.imagenRef3);

        console.log('Datos Admin procesados:', this.cita);

      console.log('1. Datos Admin:', this.cita);
      console.log('2. Datos Precios:', this.precio);
      console.log('3. Datos Base:', this.citaDTO);
    },
    error: (err) => {
      console.error('Alguna de las peticiones falló:', err);
    }
  });
}


  cerrarDetalle(): void {
    this.location.back(); 
  }

  getBadgeClass(estado: string): string {
    switch (estado) {
      case 'Pendiente': return 'bg-warning';
      case 'Rechazado': return 'bg-danger';
      default: return 'bg-success';
    }
  }
} 
