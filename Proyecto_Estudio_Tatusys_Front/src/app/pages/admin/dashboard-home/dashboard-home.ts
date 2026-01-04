import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

interface Cita {
  id: number;
  nombre: string;
  fecha: string;
  tipo: string;
  confirmada: boolean;
}

@Component({
  selector: 'app-dashboard-home',
  imports: [CommonModule],
  templateUrl: './dashboard-home.html',
  styleUrl: './dashboard-home.css',
})
export class DashboardHome {

// Lista de citas pendientes (las que aún no han sido confirmadas)
  citas = [
    { id: 101, nombre: 'Nombre Apellido1 y Apellido2', fecha: '1 de Diciembre - 10:00', tipo: 'Tatuaje' },
    { id: 102, nombre: 'Laura Martínez', fecha: '1 de Diciembre - 12:00', tipo: 'Piercing' },
    { id: 103, nombre: 'Marcos Soler', fecha: '2 de Diciembre - 17:00', tipo: 'Tatuaje' },
  ];

  constructor(private router: Router) {}

  irAConfirmar(id: number) {
    // Navegamos a la página de confirmación pasando el ID
    // Ejemplo: /dashboard/confirmar/101
    this.router.navigate(['/confirmar', id]);
  }

  verDetalles(id: number) {
    this.router.navigate(['/detalles', id]);
  }
}
