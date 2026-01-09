import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { ActivatedRoute } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';

@Component({
  selector: 'app-appointment-details',
  imports: [CommonModule, FormatoHorasPipe],
  templateUrl: './appointment-details.html',
  styleUrls: ['./appointment-details.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppointmentDetails implements OnInit {
  cita!: AppointmentAdminDTO;

  constructor(
    private appointmentService: AppointmentService, 
    private route: ActivatedRoute,
    private location: Location
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
