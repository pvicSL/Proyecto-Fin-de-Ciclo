import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Verificamos si es administrador usando el Signal de tu servicio
  if (authService.isAdmin()) {
    return true; // Acceso concedido
  }

  // Si no es admin, lo redirigimos a una ruta segura
  console.warn('Intento de acceso no autorizado a ruta administrativa');
  router.navigate(['/admin/citas']); 
  return false;
};