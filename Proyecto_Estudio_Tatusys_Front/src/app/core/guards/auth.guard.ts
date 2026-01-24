import { inject } from "@angular/core";
import { CanActivateFn, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";

// auth.guard.ts (Para TODOS los empleados logueados)
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.currentUser()) {
    return true; // Si hay sesión, adelante (sea admin o trabajador)
  }

  router.navigate(['/login']);
  return false;
};