import { Component } from '@angular/core';

import { RouterLink, RouterModule } from '@angular/router';

interface NavItem {
  title: string;
  link: string;
  icon: string;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  isExpanded = false;

  // Lista de navegación centralizada
  navItems: NavItem[] = [
    { title: 'Home', link: '/admin', icon: 'bi-house-door-fill' },
    { title: 'Solicitudes', link: '/admin/solicitudes', icon: 'bi-envelope-fill' },
    { title: 'Facturas', link: '/facturas', icon: 'bi-file-earmark-ruled-fill' },
    { title: 'Calendario', link: '/calendario', icon: 'bi-calendar2-event-fill' },
    { title: 'Trabajadores', link: '/trabajadores', icon: 'bi-people-fill' }
  ];

  footerItems: NavItem[] = [
    { title: 'Configuración', link: '/config', icon: 'bi-gear-wide-connected' },
    { title: 'Cerrar Sesión', link: '/logout', icon: 'bi-box-arrow-left' }
  ];

  toggleSidebar() {
    this.isExpanded = !this.isExpanded;
  }
}
