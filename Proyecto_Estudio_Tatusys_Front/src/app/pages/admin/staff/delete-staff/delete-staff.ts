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
      next: (response: any) => {
        // Al entrar en 'next', sabemos que el servidor respondió con un 200 OK
        // En tu Java, el caso 0 devuelve ResponseEntity.ok()
        alert('Trabajador eliminado con éxito');
        window.history.back();
      },
      error: (err) => {
        // Aquí manejamos los casos de error (400, 404, etc.)
        if (err.status === 404) {
          alert('Error: El trabajador ya no existe en la base de datos.');
        } else if (err.status === 400) {
          // Extraemos el mensaje de error que pusiste en el ResponseEntity de Java
          const mensajeError = err.error?.error || 'El trabajador tiene citas asignadas.';
          alert(mensajeError);
        } else {
          alert('El servidor rechazó la petición. Verifica la conexión.');
        }
        console.error('Detalles del error:', err);
      }
    });
  }
}

  cancelar() {
    window.history.back();
  }
}