import { Component, inject } from '@angular/core';

import { Router, RouterLink, RouterModule } from '@angular/router';
import { AuthService } from '../../../../core/services/auth.service';

interface NavItem {
  title: string;
  link: string;
  icon: string;
  onlyAdmin?: boolean;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterModule],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  isExpanded = false;
  // Inyectamos el servicio de auth
  public authService = inject(AuthService);
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
  private allNavItems: NavItem[] = [
    { title: 'Home', link: '/admin/citas', icon: 'bi-house-door-fill' },
    { title: 'Solicitudes', link: '/admin/solicitudes', icon: 'bi-envelope-fill' },
    { title: 'Facturas', link: '/facturas', icon: 'bi-file-earmark-ruled-fill' },
    { title: 'Calendario', link: '/admin/calendario', icon: 'bi-calendar2-event-fill' },
    // Solo para admins
    { title: 'Trabajadores', link: '/admin/trabajadores', icon: 'bi-people-fill' }
  ];

  private allFooterItems: NavItem[] = [
    // Solo para admins
    { title: 'Configuración', link: '/admin/ajustes', icon: 'bi-gear-wide-connected' },
    { title: 'Cerrar Sesión', link: '/logout', icon: 'bi-box-arrow-left' }
  ];

  // Getter para obtener solo los items permitidos para el usuario actual
  get navItems(): NavItem[] {
    return this.allNavItems.filter(item => !item.onlyAdmin || this.authService.isAdmin());
  }

  get footerItems(): NavItem[] {
    return this.allFooterItems.filter(item => !item.onlyAdmin || this.authService.isAdmin());
  }

  // Modifica el método de logout para usar tu servicio
  handleLogout() {
    this.authService.logout();
  }

  toggleSidebar() {
    this.isExpanded = !this.isExpanded;
  }

  cerrarSidebarMobile() {
  const sidebar = document.getElementById('sidebar-mobile');
  
  // Verificamos si el sidebar tiene la clase 'show' (está abierto)
  if (sidebar && sidebar.classList.contains('show')) {
    // Buscamos el botón de cerrar que ya tienes o disparamos el toggle
    const bootstrap = (window as any).bootstrap;
    if (bootstrap) {
      const bsCollapse = new bootstrap.Collapse(sidebar);
      bsCollapse.hide();
    } else {
      // Opción de respaldo: forzar el click en el botón de cerrar si no tienes acceso al objeto bootstrap
      const closeBtn = sidebar.querySelector('.btn-close') as HTMLElement;
      closeBtn?.click();
    }
  }
}
}
