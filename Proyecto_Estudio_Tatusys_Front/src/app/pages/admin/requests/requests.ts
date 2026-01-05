import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AppointmentService} from '../../../core/services/appointment.service';
import { Router, RouterModule } from '@angular/router';
import { AppointmentDTO } from '../../../core/models/appointment.model';

@Component({
  selector: 'app-requests',
  imports: [CommonModule, RouterModule],
  templateUrl: './requests.html',
  styleUrl: './requests.css',
  encapsulation: ViewEncapsulation.None
})
export class Requests {
  // Filtro de Solicitudes
  // Estado inicial
  filtroSeleccionado: 'pendientes' | 'revisadas' | 'rechazadas' = 'pendientes';

  cambiarFiltro(tipo: 'pendientes' | 'revisadas' | 'rechazadas') {
    this.filtroSeleccionado = tipo;
    
    // Aquí puedes llamar a tu servicio para recargar la tabla
    console.log('Filtrando por:', tipo);
    // this.cargarCitas(tipo); 
  }
  
  citasConfirmadas: AppointmentDTO[] = [];
  paginaActual: number = 1;

  constructor(
    private appointmentService: AppointmentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarCitas();
  }

  /**
   * Carga la lista completa de la API sin filtros de tiempo
   */
  cargarCitas(): void {
    // Usamos el método de tu servicio que trae todas las confirmadas
    this.appointmentService.getRequests().subscribe({
      next: (data) => {
        this.citasConfirmadas = data;
        this.paginaActual = 1; // Reiniciamos a la primera página tras cargar
      },
      error: (error) => console.error('Error al obtener citas:', error)
    });
  }

  // --- Lógica de Paginación ---

  get citasPorPagina(): number {
    return window.innerWidth < 991.98 ? 5 : 6;
  }

  get citasPaginadas() {
    const inicio = (this.paginaActual - 1) * this.citasPorPagina;
    const fin = inicio + this.citasPorPagina;
    return this.citasConfirmadas.slice(inicio, fin);
  }

  get totalPaginas(): number[] {
    const total = Math.ceil(this.citasConfirmadas.length / this.citasPorPagina);
    return Array.from({ length: total }, (_, i) => i + 1);
  }

  irAPagina(pagina: number | string): void {
    if (pagina === 'prev' && this.paginaActual > 1) {
        this.paginaActual--;
    } else if (pagina === 'next' && this.paginaActual < this.totalPaginas.length) {
        this.paginaActual++;
    } else if (typeof pagina === 'number') {
        this.paginaActual = pagina;
    }
  }

  // --- Navegación ---

  revisarPresupuesto(id: number) {
    this.router.navigate(['/admin/solicitudes/revisarPresupuesto', id]);
  }
}
