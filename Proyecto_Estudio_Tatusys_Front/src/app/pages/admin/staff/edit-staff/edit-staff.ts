import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService } from '../../../../core/services/staff.service';
import { StaffAdminDTO } from '../../../../core/models/staff-admin.model';

@Component({
  selector: 'app-edit-staff',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-staff.html',
  styleUrls: ['./edit-staff.css'],
  encapsulation: ViewEncapsulation.None
})
export class EditStaff implements OnInit {

  staff!: StaffAdminDTO;
  datosOriginales: string = ''; // Para detectar cambios
  hayCambios: boolean = false;

  constructor(
    private staffService: StaffService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const dniUrl = this.route.snapshot.params['dni'];
    this.cargarDatosStaff(dniUrl);
  }

  cargarDatosStaff(dni: string) {
  this.staffService.getStaffByDni(dni).subscribe({
    next: (data) => {
      this.staff = data;
      this.datosOriginales = JSON.stringify(data);
      this.hayCambios = false;
    },
    error: (err) => {
      console.error('Error al conectar con la API:', err);
      // Si la API devuelve el 404 que programaste, saldrá aquí
    }
  });
}

  // Se llama en cada input del HTML con (input)
  verificarCambios() {
    this.hayCambios = JSON.stringify(this.staff) !== this.datosOriginales;
  }

  guardarStaff() {
  if (confirm('¿Deseas actualizar los datos del trabajador?')) {
    
    // Creamos el objeto manualmente para GARANTIZAR que no lleve 'citas'
    const staffParaEnviar = {
      idTrabajador: this.staff.idTrabajador,
      nombre: this.staff.nombre,
      apellido1: this.staff.apellido1,
      apellido2: this.staff.apellido2,
      dni: this.staff.dni,
      email: this.staff.email,
      telefono: this.staff.telefono,
      numeroCuenta: this.staff.numeroCuenta,
      funciones: this.staff.funciones,
      rol: this.staff.rol,
      contrasenia: this.staff.contrasenia,
    };

    console.log('Enviando a la API:', staffParaEnviar);

    this.staffService.updateStaff(this.staff.idTrabajador, staffParaEnviar).subscribe({
      next: () => {
        alert('Datos actualizados correctamente');
        this.hayCambios = false;
        window.history.back();
      },
      error: (err) => {
        console.error('Error en la petición:', err);
        alert('Error al actualizar: El servidor no pudo procesar la solicitud.');
      }
    });
  }
}

  cancelar() {
    if (this.hayCambios) {
      if (confirm('Tienes cambios sin guardar. ¿Deseas salir de todas formas?')) {
        window.history.back();
      }
    } else {
      window.history.back();
    }
  }
}