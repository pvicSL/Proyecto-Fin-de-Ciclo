import { Component, inject } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { AppointmentService } from '../../../core/services/appointment.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [],
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
    next: (res) => {
      // res contiene { token, message } según tu Java actual
      
      // IMPORTANTE: Si el Java no envía el rol, el sidebar no se filtrará.
      // Por ahora guardamos lo que llega:
      this.authService.saveSession({
        token: res.token,
        email: this.email, // Lo tomamos del input del formulario
        rol: 'ADMIN',      // <--- OJO: Aquí habría que recibirlo del backend
        nombre: 'Usuario',
        id: 0
      });
      
      this.router.navigate(['/admin/citas']);
    },
    error: (err) => {
      this.isLoading = false;
      alert('Error: ' + (err.error?.message || 'Credenciales inválidas'));
    }
  });
}

}
