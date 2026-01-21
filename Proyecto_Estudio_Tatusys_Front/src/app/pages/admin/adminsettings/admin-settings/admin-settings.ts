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
    const id = this.route.snapshot.params['id'];
      this.cargarDatosStaff(id);

  }

  cargarDatosStaff(id: number) {
    // Aquí usarías el método de tu service para traer un solo trabajador
    this.staffService.getStaffById(id).subscribe({
      next: (data) => this.staff = data,
      error: (err) => console.error('Error al cargar trabajador', err)
    });
  }

  guardarStaff() {
      this.staffService.updateStaff(this.staff.idTrabajador, this.staff).subscribe({
        next: () => {
          alert('Datos actualizados correctamente');
          window.history.back();
        },
        error: (err) => alert('Error: ' + err.error?.message)
      });
    }

  cancelar() {
    window.history.back();
  }
}
