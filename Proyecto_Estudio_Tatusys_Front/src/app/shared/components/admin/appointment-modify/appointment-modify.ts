import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BudgetService } from '../../../../core/services/budget.service';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { ActivatedRoute } from '@angular/router';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';

@Component({
  selector: 'app-appointment-modify',
  imports: [CommonModule, FormsModule, FormatoHorasPipe],
  templateUrl: './appointment-modify.html',
  styleUrls: ['./appointment-modify.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppointmentModify implements OnInit {

  cita!: AppointmentAdminDTO;

  constructor(
    private budgetService: BudgetService,
    private appointmentService: AppointmentService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.cargarDatos();
  }

  cargarDatos() {
    const id = this.route.snapshot.params['id'];
    this.appointmentService.getDetalleCita(id).subscribe(datosCita => {
      this.budgetService.obtenerPresupuestoPorCita(id).subscribe(datosPresupuesto => {
        this.cita = {
          ...datosCita,
          ...datosPresupuesto
        };
        console.log('Datos unificados:', this.cita);
      });
    });
  }

  guardarPresupuesto() {
    if (this.cita && this.cita.idServicio) {
      this.budgetService.actualizarPresupuestoConExtra(this.cita.idServicio, this.cita)
        .subscribe({
          next: (res) => {
            this.cita = res; 
            alert('¡Presupuesto actualizado correctamente!');
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
    window.history.back();
  }
} 
