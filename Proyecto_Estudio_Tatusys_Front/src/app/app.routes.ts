import { Routes } from '@angular/router';
import { ClientLayoutComponent } from './shared/layouts/client-layout/client-layout';
import { HomeComponent } from './pages/client/home/home';
import { NotFoundComponent } from './pages/public/not-found/not-found';

export const routes: Routes = [
  {
    path: '404',
    component: NotFoundComponent
  },


  // 1. RUTA CLIENTE FINAL
  {
    path: '',
    component: ClientLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      // Aquí podrías añadir más rutas públicas en el futuro

    ]
  },

  // 2. RUTA ADMIN

  {
    path: 'admin',
    loadComponent: () => import('./shared/layouts/admin-layout/admin-layout')
      .then(m => m.AdminLayout),

    children: [
      {
        path: '',
        // Carga el dashboard por defecto al entrar en /admin
        loadComponent: () => import('./pages/admin/dashboard-home/dashboard-home').then(m => m.DashboardHome),
        data: { title: 'Home', icon: 'bi-house-door' }
      },
      // RUTAS FUTURAS DEL DASHBOARD
      {
        path: 'solicitudes',
        loadComponent: () => import('./pages/admin/requests/requests').then(m => m.Requests),
        data: { title: 'Solicitudes', icon: 'bi-envelope' }
      },

      // Comodín Específico de ADMIN
      // Si la URL empieza por '/admin/' pero lo siguiente no existe, redirige al Dashboard (/admin)
      { path: '**', redirectTo: '' }
    ]

  },

  // RUTA DE RESPALDO GLOBAL - Si hay cualquier ruta de error
  { path: '**', redirectTo: '/404' }
];