import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import { AppointmentService} from '../../../core/services/appointment.service';
import { Router, RouterModule } from '@angular/router';
import { AppointmentDTO } from '../../../core/models/appointment.model';
import { LayoutService } from '../../../shared/services/layout.service';

@Component({
  selector: 'app-requests',
  imports: [CommonModule, RouterModule],
  templateUrl: './requests.html',
  styleUrl: './requests.css',
  encapsulation: ViewEncapsulation.None
})
export class Requests {

  constructor(private router: Router, public layoutService: LayoutService) {}
  // Filtro de Solicitudes
  // Estado inicial
  filtroSeleccionado: 'pendientes' | 'revisadas' = 'pendientes';

  cambiarFiltro(tipo: 'pendientes' | 'revisadas' ) {
    this.filtroSeleccionado = tipo;
    if (tipo === 'pendientes') {
      this.router.navigate(['/admin/solicitudes']); // Cambia por tu ruta real
    } else {
      this.router.navigate(['/admin/solicitudes/revisadas']);  // Cambia por tu ruta real
    }
  }


}
