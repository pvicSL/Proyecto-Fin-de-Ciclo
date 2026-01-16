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
    this.cargarAlertas();
  }

  /**
   * Carga los trabajadores desde la base de datos
   */
  cargarStaff(): void {
    this.staffService.getStaff().subscribe({
      next: (data: StaffDTO[]) => {
        this.listaStaff = data;
        console.log('Staff cargado:', this.listaStaff);
      },
      error: (err) => {
        console.error('Error al cargar el staff desde la BBDD', err);
      }
    });
  }

  cargarAlertas() {
    this.appointmentService.getRequests().subscribe({
      next: (data) => this.numeroPendientes = data.length,
      error: (err) => console.error(err)
    });
  }

  // --- Navegación ---

  irANuevoMiembro(): void {
    this.router.navigate(['admin/staff/nuevo']);
  }

  editarStaff(id: number): void {
    this.router.navigate(['admin/staff/editar', id]);
  }

  verDetallesStaff(id: number): void {
    this.router.navigate(['admin/staff/detalles', id]);
  }

  // --- Lógica de Paginación Dinámica ---
  
  get itemsPorPagina(): number {
    const altoVentana = window.innerHeight;
    const espacioOcupado = window.innerWidth < 992 ? 380 : 340;
    const altoFila = window.innerWidth < 576 ? 80 : 65; 
    return Math.max(4, Math.floor((altoVentana - espacioOcupado) / altoFila));
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