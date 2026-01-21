import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { LayoutService } from '../../../shared/services/layout.service';

@Component({
  selector: 'app-adminsettings',
  imports: [RouterOutlet, CommonModule],
  templateUrl: './adminsettings.html',
  styleUrl: './adminsettings.css',
})
export class Adminsettings {

  constructor(private router: Router, public layoutService: LayoutService) {}
  // Filtro de Solicitudes
  // Estado inicial
  filtroSeleccionado: 'admin' | 'servicios' | 'precios' = 'admin';

  cambiarFiltro(tipo: 'admin' | 'servicios' | 'precios' ) {
    this.filtroSeleccionado = tipo;
    if (tipo === 'admin') {
      this.router.navigate(['/admin/ajustes/admin']); 
    } else if (tipo === 'servicios') {
      this.router.navigate(['/admin/ajustes/servicios']);  
    } else {
      this.router.navigate(['/admin/ajustes/precios']);
    }
  }

}
