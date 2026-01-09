import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import { AppointmentService} from '../../../../core/services/appointment.service';
import { Router, RouterModule } from '@angular/router';
import { AppointmentDTO } from '../../../../core/models/appointment.model';
import { BudgetService } from '../../../../core/services/budget.service';
@Component({
  selector: 'app-pending-requests',
  imports: [],
  templateUrl: './pending-requests.html',
  styleUrls: ['./pending-requests.css'],
  encapsulation: ViewEncapsulation.None
})
export class PendingRequests {
  
  citasPendientes: AppointmentDTO[] = [];
  paginaActual: number = 1;

  constructor(
    private appointmentService: AppointmentService,
    private budgetService: BudgetService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarCitas();
  }

  /**
   * Carga la lista completa de la API sin filtros de tiempo
   */
  cargarCitas(): void {
    // Usamos el método de tu servicio que trae todas las pendientes
    this.budgetService.obtenerPorEstadoPresupuestoPendiente().subscribe({
      next: (data) => {
        this.citasPendientes = data;
        this.paginaActual = 1; // Reiniciamos a la primera página tras cargar
      },
      error: (error) => console.error('Error al obtener citas:', error)
    });
  }



  get citasPorPagina(): number {
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
  return Math.max(6, filasCalculadas);
}

  @HostListener('window:resize')
  onResize() {
  // Forzamos el recálculo y volvemos a la primera página para evitar errores visuales
  this.paginaActual = 1;
}

  get citasPaginadas() {
    const inicio = (this.paginaActual - 1) * this.citasPorPagina;
    const fin = inicio + this.citasPorPagina;
    return this.citasPendientes.slice(inicio, fin);
  }

  get totalPaginas(): number[] {
    const total = Math.ceil(this.citasPendientes.length / this.citasPorPagina);
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
    this.router.navigate(['/admin/solicitudes/pendientes/revisar', id]);
  }
  
}
