import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Router, RouterLink } from '@angular/router';
import { LoginResponse } from '../../../core/models/loginResponse.model';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  // Datos del formulario
  email = '';
  password = '';
  recuerdame = false;
  
  // Estado de la interfaz
  showPassword = false;
  isLoading = false;

  private authService = inject(AuthService);
  private appointmentService = inject(AppointmentService);
  private router = inject(Router);

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onLogin() {
  if (!this.email || !this.password) return;

  this.isLoading = true;
  const credentials = { email: this.email, password: this.password };

  this.appointmentService.postLogin(credentials).subscribe({
    next: (res: LoginResponse) => {
      // 1. Guardamos el token y los datos del usuario
      // Si el backend aún no envía 'rol', le ponemos 'ADMIN' por defecto para poder testear
      this.authService.saveSession({
        token: res.token,
        message: res.message,
        id: res.id || 0,
        nombre: res.nombre || 'Usuario',
        email: res.email || this.email,
        // Forzamos el tipo para que coincida con lo que espera saveSession
        rol: (res.rol || 'ADMIN') as 'ADMIN' | 'TRABAJADOR'
      });

      // 2. Navegamos al panel principal
      this.router.navigate(['/admin/citas']);
    },
    error: (err) => {
      this.isLoading = false;
      console.error('Error en el login:', err);
      alert(err.error?.message || 'Error de conexión con el servidor');
    }
  });
}

}
