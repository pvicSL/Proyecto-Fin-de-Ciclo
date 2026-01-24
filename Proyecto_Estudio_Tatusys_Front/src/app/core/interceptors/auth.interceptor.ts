import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Buscamos el token en el localStorage
  const token = localStorage.getItem('token');

  // Si el token existe, clonamos la petición y le añadimos el encabezado
  if (token) {
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(authReq);
  }

  // Si no hay token (como en la petición de login), la enviamos tal cual
  return next(req);
};