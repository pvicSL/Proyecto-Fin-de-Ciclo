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
        next: () => {
          alert('Trabajador eliminado con éxito');
          window.history.back(); // O la ruta de tu lista
        },
        error: (err) => {
          console.error(err);
          alert('Error al intentar eliminar: ' + (err.error?.message || 'Servidor no disponible'));
        }
      });
    }
  }

  cancelar() {
    window.history.back();
  }
}