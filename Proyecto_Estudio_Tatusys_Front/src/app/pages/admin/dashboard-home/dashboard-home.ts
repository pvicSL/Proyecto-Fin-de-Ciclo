import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { AppointmentService} from '../../../core/services/appointment.service';
import { Router, RouterModule } from '@angular/router';
import { AppointmentDTO } from '../../../core/models/appointment.model';


@Component({
  selector: 'app-dashboard-home',
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-home.html',
  styleUrl: './dashboard-home.css',
  encapsulation: ViewEncapsulation.None
})
export class DashboardHome implements OnInit{

  // Lista de citas confirmadas
    
  citasConfirmadas: AppointmentDTO[] = [];
  
  // --- Estado para los botones ---
  fechaActual: Date = new Date();
  vista: 'dia' | 'semana' = 'dia';

  constructor(
    private appointmentService: AppointmentService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarCitas();
  }

  // --- Lógica de navegación ---

  /**
   * Cambia el modo de visualización y refresca los datos
   */
  setVista(tipo: 'dia' | 'semana'): void {
    this.vista = tipo;
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
    this.router.navigate(['/confirmar', id]);
  }

  verDetalles(id: number) {
    this.router.navigate(['/detalles', id]);
  }

  paginaActual: number = 1;

  // Modificamos esta variable para que sea dinámica
  get citasPorPagina(): number {
    // Si el ancho es menor a 768 (móvil/tablet pequeña), mostramos 5, si no 7
    return window.innerWidth < 991.98 ? 5 : 6;
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
}


}
