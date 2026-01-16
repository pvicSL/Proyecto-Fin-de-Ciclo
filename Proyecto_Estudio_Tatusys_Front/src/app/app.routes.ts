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
      // Redirige automáticamente /admin -> /admin/citas
      { path: '', redirectTo: 'citas', pathMatch: 'full' },

      {
        path: 'citas',
        loadComponent: () => import('./pages/admin/dashboard-home/dashboard-home').then(m => m.DashboardHome),
        children: [
          { 
            path: '',
            loadComponent: () => import('./pages/admin/dashboard-home/appointments/appointments').then(m => m.Appointments),
            data: {title: 'Citas', icon: 'bi-house-door'}
          },
          { 
            path: 'detalleCitaConfirmada/:id',
            loadComponent: () => import('./pages/admin/dashboard-home/appointment-detail/appointment-detail').then(m => m.AppointmentDetail),
            data: {title: 'Home > Detalle Cita Confirmada'}
          },
          {
            path: 'confirmarPresupuesto/:id',
            loadComponent: () => import('./pages/admin/dashboard-home/appointment-confirm/appointment-confirm').then(m => m.AppointmentConfirm),
            data: {title: 'Home > Confirmar Presupuesto'}
          }
        ]
      },

      {
        path: 'solicitudes',
        loadComponent: () => import('./pages/admin/requests/requests').then(m => m.Requests),
        data: { title: 'Solicitudes', icon: 'bi-envelope' },
        children: [
          { path: '', redirectTo: 'pendientes', pathMatch: 'full' },
          {
            path: 'pendientes', 
            loadComponent: () => import('./pages/admin/requests/pending-requests/pending-requests').then(m => m.PendingRequests), 
            data: {title: 'Solicitudes > Pendientes', icon: 'bi-envelope'}
          },
          {
            path: 'revisadas', 
            loadComponent: () => import('./pages/admin/requests/reviewed-requests/reviewed-requests').then(m => m.ReviewedRequests), 
            data: {title: 'Solicitudes > Revisadas', icon: 'bi-envelope'}
          },
          {
            path: 'revisadas/detalle/:id', 
            loadComponent: () => import('./pages/admin/requests/reviewed-requests-detail/reviewed-requests-detail').then(m => m.ReviewedRequestsDetail), 
            data: {title: 'Solicitudes > Revisadas > Detalle', icon: 'bi-envelope'}
          },
          {
            path: 'pendientes/revisar/:id', 
            loadComponent: () => import('./pages/admin/requests/generate-budget/generate-budget').then(m => m.GenerateBudget), 
            data: {title: 'Solicitudes > Pendientes > Revisar', icon: 'bi-envelope'}
          }
        ]
      },

      {
        path: 'trabajadores', 
        loadComponent: () => import('./pages/admin/staff/staff').then(m => m.Staff), 
        data: {title: 'Trabajadores', icon: 'bi-people'},

        children: [
          { path: '', redirectTo: 'list', pathMatch: 'full' },

          {
            path: 'list', 
            loadComponent: () => import('./pages/admin/staff/list-staff/list-staff').then(m => m.ListStaff), 
            data: {title: 'Staff', icon: 'bi-people'}
          }
        ]
      },

      // Comodín Específico de ADMIN -> lleva a /admin/citas
      { path: '**', redirectTo: 'citas' }
    ]

  },

  // RUTA DE RESPALDO GLOBAL - Si hay cualquier ruta de error
  { path: '**', redirectTo: '/404' }
];