import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Buscamos el token en el localStorage
  const token = localStorage.getItem('token');
  console.log('>>> PETICIÓN INTERCEPTADA:', req.url); // Añade esto

  // Si el token existe, clonamos la petición y le añadimos el encabezado
  if (token) {
    console.log('>>> AÑADIENDO TOKEN A LA CABECERA'); // Y estong serve -o
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