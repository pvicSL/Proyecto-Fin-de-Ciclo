import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withComponentInputBinding, withViewTransitions } from '@angular/router';
import { provideHttpClient, withFetch } from '@angular/common/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),

    // 1. Configuración del Router mejorada
    // 'withComponentInputBinding' facilita leer IDs de la URL en el Dashboard
    // 'withViewTransitions' da un efecto suave al cambiar de página
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),

    // 2. HABILITAR CONEXIÓN CON SPRING BOOT
    // Sin esto, los servicios que se creen en 'core/services' no funcionarán.
    provideHttpClient(withFetch())
  ]
};