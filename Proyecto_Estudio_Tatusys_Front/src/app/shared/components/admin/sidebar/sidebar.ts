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
  // Obtenemos la URL actual limpia
  const currentUrl = this.router.url;

  // Si el link es solo '/admin', evitamos que marque todo
  if (itemLink === '/admin') {
    return currentUrl === '/admin';
  }

  // Si la URL actual empieza por el link del item, se marca como activo
  // Esto cubrirá: /admin/calendario, /admin/solicitudes, etc.
  return currentUrl.startsWith(itemLink);
}


  // Lista de navegación centralizada
private allNavItems: NavItem[] = [
  { title: 'Home', link: '/admin/citas', icon: 'bi-house-door-fill' },
  { title: 'Solicitudes', link: '/admin/solicitudes', icon: 'bi-envelope-fill' },
  { title: 'Calendario', link: '/admin/calendario', icon: 'bi-calendar2-event-fill' },
  { title: 'Trabajadores', link: '/admin/trabajadores', icon: 'bi-people-fill', onlyAdmin: true } // <--- SOLO ADMIN
];

private allFooterItems: NavItem[] = [
  { title: 'Configuración', link: '/admin/ajustes', icon: 'bi-gear-wide-connected', onlyAdmin: true }, // <--- SOLO ADMIN
  { title: 'Cerrar Sesión', link: '/logout', icon: 'bi-box-arrow-left' }
];

// 2. Este es el motor que limpia el sidebar
get navItems(): NavItem[] {
  const userRole = this.authService.currentUser()?.rol;
  // Si es ADMIN, ve todo. Si no, filtramos los que tengan 'onlyAdmin'
  return this.allNavItems.filter(item => {
    if (item.onlyAdmin && userRole !== 'ADMIN') return false;
    return true;
  });
}

get footerItems(): NavItem[] {
  const userRole = this.authService.currentUser()?.rol;
  return this.allFooterItems.filter(item => {
    if (item.onlyAdmin && userRole !== 'ADMIN') return false;
    return true;
  });
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
