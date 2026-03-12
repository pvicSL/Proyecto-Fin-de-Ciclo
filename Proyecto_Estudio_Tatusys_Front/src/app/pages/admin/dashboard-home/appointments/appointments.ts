import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import { AppointmentService} from '../../../../core/services/appointment.service';
import { Router, RouterModule } from '@angular/router';
import { AppointmentDTO } from '../../../../core/models/appointment.model';
import { AppointmentDetails } from '../../../../shared/components/admin/appointment-details/appointment-details';
import { PricesService } from '../../../../core/services/prices.service';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';


@Component({
  selector: 'appointments',
  imports: [CommonModule, RouterModule, FormatoHorasPipe],
  templateUrl: 'appointments.html',
  styleUrl: 'appointments.css',
  encapsulation: ViewEncapsulation.None
})
export class Appointments implements OnInit{

  // Lista de citas confirmadas
    
  citasConfirmadas: AppointmentDTO[] = [];
  
  // --- Estado para los botones ---
  fechaActual: Date;
  vista: 'dia' | 'semana';
  paginaActual: number;

  constructor(
    private appointmentService: AppointmentService,
    private router: Router,
    private stateService: AppointmentService,
  ) {{
    // Recuperamos el estado guardado
    this.fechaActual = this.stateService.fechaActual;
    this.vista = this.stateService.vista;
    this.paginaActual = this.stateService.paginaActual;
  }}

  ngOnInit(): void {
    this.cargarCitas();
    this.cargarCitasPendientes();
    this.cargarNumeroCitas();
  }

  // --- Lógica de navegación ---

  /**
   * Cambia el modo de visualización y refresca los datos
   */
  setVista(tipo: 'dia' | 'semana'): void {
    this.vista = tipo;
    this.stateService.vista = tipo; // Actualizamos el servicio
    this.paginaActual = 1;
    this.stateService.paginaActual = 1;
    this.cargarCitas();
  }

  /**
   * Mueve la fecha hacia adelante o atrás
   * @param direccion 1 para derecha, -1 para izquierda
   */
  navegar(direccion: number): void {
    const paso = this.vista === 'dia' ? 1 : 7;
    const nuevaFecha = new Date(this.fechaActual);
    nuevaFecha.setDate(nuevaFecha.getDate() + (direccion * paso));
    this.fechaActual = nuevaFecha;
    this.stateService.fechaActual = nuevaFecha;
    
    this.cargarCitas();
  }

  /**
   * Formatea el texto del encabezado según la vista actual
   */
  get rangoFechasTexto(): string {
    const locale = 'es-ES';
    if (this.vista === 'dia') {
      return this.fechaActual.toLocaleDateString(locale, { day: 'numeric', month: 'long' });
    } else {
      const inicio = new Date(this.fechaActual);
      const fin = new Date(this.fechaActual);
      fin.setDate(fin.getDate() + 6);
      
      return `${inicio.getDate()} ${inicio.toLocaleString(locale, { month: 'short' })} - ${fin.getDate()} ${fin.toLocaleString(locale, { month: 'short' })}`;
    }
  }

  // --- Lógica de datos y navegación de rutas ---

  
  cargarCitas(): void {
  // Formateamos la fecha para que Java la entienda (YYYY-MM-DD)
  const fechaISO = this.fechaActual.toISOString().split('T')[0];

    this.appointmentService.getConfirmedAppointments(fechaISO, this.vista).subscribe({
      next: (data) => {
        this.citasConfirmadas = data;
      },
      error: (error) => console.error('Error al filtrar citas', error)
    });
  }
  irAConfirmar(id: number) {
    // Navegamos a la página de confirmación pasando el ID
    // Ejemplo: /dashboard/confirmar/101
    this.router.navigate(['admin/citas/confirmarPresupuesto', id]);
  }

  verDetalles(id: number) {
    this.router.navigate(['admin/citas/detalleCitaConfirmada', id]);
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
  return Math.max(4, filasCalculadas);
}

  @HostListener('window:resize')
  onResize() {
  // Forzamos el recálculo y volvemos a la primera página para evitar errores visuales
  this.paginaActual = 1;
}

  // lógica de citasPaginadas se mantiene igual, 
  // pero ahora usará el valor dinámico del getter anterior.
  get citasPaginadas() {
    const inicio = (this.paginaActual - 1) * this.citasPorPagina;
    const fin = inicio + this.citasPorPagina;
    return this.citasConfirmadas.slice(inicio, fin);
  }

  // El cálculo de páginas se ajusta solo al cambiar el divisor
  get totalPaginas(): number[] {
    const total = Math.ceil(this.citasConfirmadas.length / this.citasPorPagina);
    return Array.from({ length: total }, (_, i) => i + 1);
  }

// 3. Método para navegar
irAPagina(pagina: number | string): void {
    if (pagina === 'prev' && this.paginaActual > 1) {
        this.paginaActual--;
    } else if (pagina === 'next' && this.paginaActual < this.totalPaginas.length) {
        this.paginaActual++;
    } else if (typeof pagina === 'number') {
        this.paginaActual = pagina;
    }
  this.stateService.paginaActual = this.paginaActual;
}

private normalizarContador(valor: unknown): number {
  const numero = Number(valor);
  return Number.isFinite(numero) && numero > 0 ? Math.floor(numero) : 0;
}

textoConfirmadas(): string {
  const total = this.normalizarContador(this.numeroConfirmadas);
  if (total === 0) return 'No tienes citas confirmadas.';
  return `¡Tienes ${total} ${total === 1 ? 'cita confirmada!' : 'citas confirmadas!'}`;
}

textoPendientes(): string {
  const total = this.normalizarContador(this.numeroPendientes);
  if (total === 0) return 'No tienes solicitudes pendientes.';
  return `¡Tienes ${total} ${total === 1 ? 'solicitud pendiente!' : 'solicitudes pendientes!'}`;
}

numeroConfirmadas = 0;

  cargarNumeroCitas(): void{
    this.appointmentService.getNumberConfirmedAppointments().subscribe({
      next: (data) =>{
        this.numeroConfirmadas = data?.length ?? 0;
      },
      error: (err) => console.error(err)
    });
  }


numeroPendientes: number = 0;

  cargarCitasPendientes() {
    this.appointmentService.getRequests().subscribe({
      next: (data) => {
        this.numeroPendientes = data?.length ?? 0;
      },
      error: (err) => console.error(err)
    });
  }


}
