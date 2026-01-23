import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { StaffDTO } from '../../../../core/models/staff.model'; // Asegúrate de tener este DTO
import { StaffService } from '../../../../core/services/staff.service';
import { StaffAdminDTO } from '../../../../core/models/staff-admin.model';

@Component({
  selector: 'app-edit-staff',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin-settings.html',
  styleUrls: ['./admin-settings.css'],
  encapsulation: ViewEncapsulation.None
})
export class AdminSettings implements OnInit {

  staff!: StaffAdminDTO;
  


  constructor(
    private staffService: StaffService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Forzamos el ID a 1 directamente
  const idFijo = 1; 
  this.cargarDatosStaff(idFijo);

  }

  cargarDatosStaff(id: number) {
    // Aquí usarías el método de tu service para traer un solo trabajador
    this.staffService.getStaffById(id).subscribe({
      next: (data) => this.staff = data,
      error: (err) => console.error('Error al cargar trabajador', err)
    });
  }

  guardarStaff() {
      // Creamos una copia para no dañar la vista
  const datosParaEnviar = { ...this.staff };
  
  // Eliminamos la lista de citas manualmente antes de enviar 
  // para evitar que el JSON se rompa por la recursividad
  if (datosParaEnviar.citas) {
    delete (datosParaEnviar as any).citas;
  }

  this.staffService.updateStaff(this.staff.idTrabajador, datosParaEnviar).subscribe({
    next: () => {
      alert('Datos actualizados correctamente');
      window.history.back();
    },
    error: (err) => {
      console.error(err);
      alert('Error al actualizar. Revisa la consola para ver el JSON.');
    }
  });
}


}
