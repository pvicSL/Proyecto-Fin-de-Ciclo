import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit, ViewEncapsulation } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { StaffDTO } from '../../../../core/models/staff.model'; // Ajusta la ruta a tu modelo
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

  constructor(
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

  get itemsPorPagina(): number {
    const altoVentana = window.innerHeight;
    const anchoVentana = window.innerWidth;
    const espacioOcupado = anchoVentana < 992 ? 380 : 340;
    const altoFila = anchoVentana < 576 ? 80 : 65;
    const espacioDisponible = altoVentana - espacioOcupado;
    const filasCalculadas = Math.floor(espacioDisponible / altoFila);
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