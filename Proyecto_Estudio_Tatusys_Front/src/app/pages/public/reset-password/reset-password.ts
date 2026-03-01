import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule, RouterLink],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.css'] // Reutilizaremos los estilos del login
})
export class ResetPassword implements OnInit {
  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  mensaje: string = '';
  isError: boolean = false;
  loading: boolean = false;

  constructor(
    private http: HttpClient,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Capturamos el token de la URL: /reset-password?token=XYZ
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      if (!this.token) {
        this.mensaje = 'Token no válido o ausente en la URL.';
        this.isError = true;
      }
    });
  }

  actualizarPassword(): void {
    // Validación de coincidencia
    if (this.newPassword !== this.confirmPassword) {
      this.mensaje = 'Las contraseñas no coinciden';
      this.isError = true;
      return;
    }

    // Validación de longitud mínima sugerida
    if (this.newPassword.length < 6) {
      this.mensaje = 'La contraseña debe tener al menos 6 caracteres';
      this.isError = true;
      return;
    }

    this.loading = true;
    
    // POST /auth/reset-password
    const body = { 
      token: this.token, 
      nuevaContrasenia: this.newPassword 
    };

    this.http.post('http://localhost:8085/auth/reset-password', body).subscribe({
      next: (response: any) => {
        this.mensaje = 'Contraseña actualizada correctamente';
        this.isError = false;
        this.loading = false;
        // Redirigir al login después de 2 segundos
        setTimeout(() => this.router.navigate(['/login']), 2500);
      },
      error: (error) => {
        this.mensaje = error.error?.error || 'Error al actualizar contraseña';
        this.isError = true;
        this.loading = false;
      }
    });
  }
}