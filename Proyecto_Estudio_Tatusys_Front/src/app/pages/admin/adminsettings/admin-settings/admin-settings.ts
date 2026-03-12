import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffService } from '../../../../core/services/staff.service';
import { StaffAdminDTO } from '../../../../core/models/staff-admin.model';

@Component({
  selector: 'app-admin-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-settings.html',
  styleUrls: ['./admin-settings.css'],
  encapsulation: ViewEncapsulation.None
})
export class AdminSettings implements OnInit {

  staff!: StaffAdminDTO;
  datosOriginales: string = ''; // Para detectar cambios
  hayCambios: boolean = false;
  dniFijo: string = '';

  constructor(
    private staffService: StaffService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
  // 1. Empezamos buscando por el ID fijo del administrador
  this.obtenerDniYLoguear(1);
}

obtenerDniYLoguear(id: number) {
  this.staffService.getStaffById(id).subscribe({
    next: (data) => {
      // Guardamos el DNI que tiene en la base de datos
      this.dniFijo = data.dni; 
      // Ahora que tenemos el DNI, usamos tu método de siempre
      this.cargarDatosStaff(this.dniFijo);
    },
    error: (err) => console.error('No se encontró el trabajador con ID 1', err)
  });
}

// 1. Añade esta función de limpieza para que la comparación sea justa
private filtrarDatos(obj: any) {
  return JSON.stringify({
    nombre: obj.nombre,
    apellido1: obj.apellido1,
    apellido2: obj.apellido2,
    dni: obj.dni,
    email: obj.email,
    telefono: obj.telefono,
    numeroCuenta: obj.numeroCuenta,
    funciones: obj.funciones,
    rol: obj.rol,
    contrasenia: obj.contrasenia || '' // Si es undefined, lo tratamos como string vacío
  });
}

  cargarDatosStaff(dni: string) {
  this.staffService.getStaffByDni(dni).subscribe({
    next: (data) => {
        this.staff = data;
      // Si la contraseña no viene en el JSON, la inicializamos vacía para que no de undefined
      if (!this.staff.contrasenia) this.staff.contrasenia = ''; 
      
      // Guardamos la foto original filtrada (SIN CITAS)
      this.datosOriginales = this.filtrarDatos(this.staff);
        this.hayCambios = false;
      if (!this.dniFijo) this.dniFijo = data.dni;
    }
  });
}

verificarCambios() {
  // Comparamos el estado actual (filtrado) contra el original (filtrado)
  const actual = this.filtrarDatos(this.staff);
  this.hayCambios = actual !== this.datosOriginales;
}

  revertirCambios() {
  if (confirm('¿Seguro que quieres descartar los cambios y volver a los datos originales?')) {
    // Usamos el DNI que guardamos al cargar por primera vez
    this.cargarDatosStaff(this.dniFijo); 
  }
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
      },
      error: (err) => {
        console.error('Error en la petición:', err);
        alert('Error al actualizar: El servidor no pudo procesar la solicitud.');
      }
    });
  }
}


}