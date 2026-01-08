import { Component, OnInit } from '@angular/core';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { ActivatedRoute } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common'; // Importación necesaria
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';

@Component({
  selector: 'app-appointment-detail',
  imports: [CommonModule, FormatoHorasPipe],
  templateUrl: './appointment-detail.html',
  styleUrl: './appointment-detail.css',
})
export class AppointmentDetail implements OnInit {
  cita!: AppointmentAdminDTO;

  constructor(
    private appointmentService: AppointmentService, 
    private route: ActivatedRoute,
    private location: Location // Inyectamos el servicio Location.   
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.appointmentService.getDetalleCita(id).subscribe({
      next: (data) => {
        this.cita = data;
      },
      error: (err) => console.error('Error al cargar la cita', err)
    });
  }

  // FUNCIÓN PARA VOLVER ATRÁS
  cerrarDetalle(): void {
    this.location.back(); 
    // Alternativa si prefieres ir siempre a una ruta fija:
    // this.router.navigate(['/admin/appointments']);
  }
}
