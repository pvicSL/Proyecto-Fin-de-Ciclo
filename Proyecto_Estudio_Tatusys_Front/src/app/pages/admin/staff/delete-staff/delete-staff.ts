import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService } from '../../../../core/services/staff.service';

@Component({
  selector: 'app-delete-staff',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './delete-staff.html'
})
export class DeleteStaff implements OnInit {

  staff: any = null;

  constructor(
    private staffService: StaffService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    this.cargarTrabajador(id);
  }

  cargarTrabajador(id: number) {
    this.staffService.getStaffById(id).subscribe({
      next: (data) => this.staff = data,
      error: (err) => {
        console.error(err);
        alert('No se pudo encontrar el trabajador.');
        this.cancelar();
      }
    });
  }

confirmarBorrado() {
  if (this.staff && this.staff.idTrabajador) {
    this.staffService.deleteStaff(this.staff.idTrabajador).subscribe({
      next: (resultado: any) => {
        // En Java, si el método devuelve un int, llega aquí
        if (resultado === 0) {
          alert('Trabajador eliminado con éxito');
          window.history.back();
        } else if (resultado > 0) {
          // Caso: Retorna numeroCitas
          alert(`No se puede eliminar: El trabajador tiene ${resultado} citas asignadas.`);
        } else if (resultado === -1) {
          alert('Error: El trabajador ya no existe en la base de datos.');
        }
      },
      error: (err) => {
        // Si el backend lanza una excepción o devuelve 400
        console.error('Error 400 del servidor:', err);
        alert('El servidor rechazó la petición. Verifica si el trabajador tiene citas o si el ID es correcto.');
      }
    });
  }
}

  cancelar() {
    window.history.back();
  }
}