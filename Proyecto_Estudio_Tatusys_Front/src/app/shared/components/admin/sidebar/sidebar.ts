import { Component } from '@angular/core';

import { Router, RouterLink, RouterModule } from '@angular/router';

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

  constructor(private router: Router) {} // Inyectamos el router para leer la URL

  isItemActive(itemLink: string): boolean {
    const currentUrl = this.router.url;

    // Lógica especial para Home/Citas y sus hijos (detalle y presupuesto)
    if (itemLink === '/admin/citas') {
      return currentUrl === '/admin/citas' || 
             currentUrl.includes('/admin/citas/detalleCitaConfirmada/:id');
    }

    // Para el resto, basta con que la URL empiece por el link del item
    return currentUrl.startsWith(itemLink);
  }


  // Lista de navegación centralizada
  navItems: NavItem[] = [
    { title: 'Home', link: '/admin/citas', icon: 'bi-house-door-fill' },
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
