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
    /* 
   {
      path: 'admin',
      loadComponent: () => import('./shared/layouts/admin-layout/admin-layout.component')
        .then(m => m.AdminLayoutComponent),
      children: [
        {
          path: '',
          // Carga el dashboard por defecto al entrar en /admin
          loadComponent: () => import('./pages/admin/dashboard-home/dashboard-home.component')
            .then(m => m.DashboardHomeComponent)
        },
        // RUTAS FUTURAS DEL DASHBOARD:
        // { 
        //   path: 'solicitudes', 
        //   loadComponent: () => import('./pages/admin/requests/requests.component').then(m => m.RequestsComponent) 
        // },
      ]
    },
    */

    // 3. RUTA COMODÍN (Redirigir a home si la URL no existe)
    { path: '**', redirectTo: '' }
];