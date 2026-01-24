import { Injectable, signal, computed } from '@angular/core';
import { Router } from '@angular/router';
import { AuthResponse } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  // Usamos un Signal para que el Sidebar se actualice instantáneamente
  private userSignal = signal<AuthResponse | null>(null);

  // Selectores reactivos
  currentUser = computed(() => this.userSignal());
  isAdmin = computed(() => this.userSignal()?.rol === 'ADMIN');

  constructor(private router: Router) {
    // Recuperar sesión al arrancar la app
    const savedUser = localStorage.getItem('session_data');
    if (savedUser) {
      this.userSignal.set(JSON.parse(savedUser));
    }
  }

  saveSession(data: AuthResponse) {
    localStorage.setItem('session_data', JSON.stringify(data));
    localStorage.setItem('token', data.token); // El token suele ir aparte para el interceptor
    this.userSignal.set(data);
  }

  logout() {
  localStorage.removeItem('session_data');
  localStorage.removeItem('token'); // ¡Importante para el Interceptor!
  this.userSignal.set(null);
  this.router.navigate(['/login']);
}

  // Métodos para obtener los datos que necesitabas para tus rutas
  getUserId(): number | undefined {
    return this.userSignal()?.id;
  }

  getUserEmail(): string | undefined {
    return this.userSignal()?.email;
  }
}