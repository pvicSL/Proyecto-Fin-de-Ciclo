import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { StaffDTO } from '../../../../core/models/staff.model'; // Ajusta la ruta a tu modelo
import { Observable } from 'rxjs';
import { StaffService } from '../../../../core/services/staff.service';

@Component({
  selector: 'list-staff',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-staff.html',
  styleUrl: './list-staff.css',
  encapsulation: ViewEncapsulation.None
})
export class ListStaff implements OnInit {

  // Usamos el DTO que has definido
  listaStaff: StaffDTO[] = [];
  paginaActual: number = 1;
  numeroPendientes: number = 0;

  constructor(
    private appointmentService: AppointmentService,
    private staffService: StaffService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarStaff();
  }

  /**
   * Carga los trabajadores desde la base de datos
   */
  cargarStaff(): void {
    this.staffService.getStaffByRol('TRABAJADOR').subscribe({
      next: (data: StaffDTO[]) => {
        this.listaStaff = data;
        console.log('Staff cargado:', this.listaStaff);
      },
      error: (err) => {
        console.error('Error al cargar el staff desde la BBDD', err);
      }
    });
  }



  // --- Navegación ---

  irANuevoMiembro(): void {
    this.router.navigate(['admin/trabajadores/alta']);
  }

  editarStaff(dni: string): void {
    this.router.navigate(['admin/trabajadores/editar', dni]);
  }

  EliminarStaff(id: number): void {
    this.router.navigate(['admin/trabajadores/eliminar', id]);
  }

  // --- Lógica de Paginación Dinámica ---
  
  get itemsPorPagina(): number {
  const altoVentana = window.innerHeight;
  const anchoVentana = window.innerWidth;

  // 1. Definimos el espacio que NO es tabla (Header + Breadcrumb + Footer + Margen)
  // En móvil el header suele ser más alto, en desktop más bajo.
  const espacioOcupado = anchoVentana < 992 ? 380 : 340;

  // 2. Definimos cuánto mide cada fila (ajusta según tu CSS)
  const altoFila = anchoVentana < 576 ? 80 : 65; 

  // 3. Calculamos cuántas caben
  const espacioDisponible = altoVentana - espacioOcupado;
  const filasCalculadas = Math.floor(espacioDisponible / altoFila);

  // 4. Ponemos límites lógicos (mínimo 4, máximo según necesites)
  return Math.max(4, filasCalculadas);
}

  @HostListener('window:resize')
  onResize() { 
    this.paginaActual = 1; 
  }

  get staffPaginado() {
    const inicio = (this.paginaActual - 1) * this.itemsPorPagina;
    return this.listaStaff.slice(inicio, inicio + this.itemsPorPagina);
  }

  get totalPaginas(): number[] {
    const total = Math.ceil(this.listaStaff.length / this.itemsPorPagina);
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  irAPagina(pagina: number | string): void {
    if (pagina === 'prev' && this.paginaActual > 1) this.paginaActual--;
    else if (pagina === 'next' && this.paginaActual < this.totalPaginas.length) this.paginaActual++;
    else if (typeof pagina === 'number') this.paginaActual = pagina;
  }
}