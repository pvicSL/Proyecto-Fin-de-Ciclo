import { Routes } from '@angular/router';
import { ClientLayoutComponent } from './shared/layouts/client-layout/client-layout';
import { HomeComponent } from './pages/client/home/home';

export const routes: Routes = [
    // 1. RUTA CLIENTE FINAL
    {
        path: '',
        component: ClientLayoutComponent,
        children: [
            { path: '', component: HomeComponent },
            // Aquí podrías añadir más rutas públicas en el futuro
        ]
    },

    // 2. RUTA ADMIN - COMENTADA HASTA QUE ESTO ESTÉ HECHO, PARA QUE NO DÉ ERRORES DE COMPILACIÓN
    
   {
      path: 'admin',
      loadComponent: () => import('./shared/layouts/admin-layout/admin-layout')
        .then(m => m.AdminLayout),
    
      children: [
        { path: '', redirectTo: 'citas', pathMatch: 'full' },
        {
          path: 'citas',
          loadComponent: () => import('./pages/admin/dashboard-home/dashboard-home').then(m => m.DashboardHome),
          children: [
            { 
              path: '',
              loadComponent: () => import('./pages/admin/dashboard-home/appointments/appointments').then(m => m.Appointments),
              data: {title: 'Home', icon: 'bi-house-door'}
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
          // Carga la solicitudes en estado pendiente
          path: 'solicitudes', 
          loadComponent: () => import('./pages/admin/requests/requests').then(m => m.Requests), 

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
          data: {title: 'Trabajadores', icon: 'bi-people'}
        }
      ]
        
    },

    

    // 3. RUTA COMODÍN (Redirigir a home si la URL no existe)
    { path: '**', redirectTo: '' }
];