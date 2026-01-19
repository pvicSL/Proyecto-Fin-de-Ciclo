import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService } from '../../../../core/services/staff.service';
import { StaffAdminDTO } from '../../../../core/models/staff-admin.model';

@Component({
  selector: 'app-new-staff',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './new-staff.html',
  styleUrls: ['./new-staff.css'],
  encapsulation: ViewEncapsulation.None
})
export class NewStaff implements OnInit {

  staff: StaffAdminDTO = {
  idTrabajador: 0, // O null si el backend lo permite
  nombre: '',
  apellido1: '',
  apellido2: '',
  dni: '',
  email: '',
  telefono: '',
  numeroCuenta: '',
  contrasenia: '',
  rol: 'TRABAJADOR',
  funciones: 'CREACION',
  citas: [] // Enviamos el array vacío como pediste
};

  constructor(
    private staffService: StaffService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // No necesitamos cargar nada, el objeto ya está inicializado arriba
  }

  guardarStaff() {
    // Usamos el método de "crear" (POST) en lugar del de "actualizar" (PUT)
    this.staffService.createStaff(this.staff).subscribe({
      next: () => {
        alert('Trabajador creado correctamente');
        window.history.back();
      },
      error: (err) => alert('Error al crear: ' + (err.error?.message || 'Servidor no disponible'))
    });
  }

  cancelar() {
    window.history.back();
  }
}