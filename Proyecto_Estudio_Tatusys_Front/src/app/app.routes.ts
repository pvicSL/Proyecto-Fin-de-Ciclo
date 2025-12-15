import { Routes } from '@angular/router';
import { ClientLayout } from './shared/client-layout/client-layout';
import { HomeComponent } from './pages/home/home';

export const routes: Routes = [
    {
        path: '',
        component: ClientLayout, // 1. Carga el envoltorio primero
        children: [
            // 2. Dentro del envoltorio, carga la Home
            { path: '', component: HomeComponent },
            // En el futuro: { path: 'citas', component: CitasComponent }
        ]
    },

    // --- FUTURO: ZONA ADMIN ---
    // {
    //    path: 'admin',
    //    component: AdminLayoutComponent, // El envoltorio de tu compañero
    //    loadChildren: () => import('./features/admin/admin.module').then(m => m.AdminModule)
    // }
];
