import { Component } from '@angular/core';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.css']
})
export class ForgotPassword {
  email: string = '';
  loading: boolean = false;
  mensaje: string = '';
  isError: boolean = false;

  constructor(private http: HttpClient) {}

  solicitarToken(): void {
    if (!this.email) {
      this.mensaje = 'El email es requerido';
      this.isError = true;
      return;
    }

    this.loading = true;
    this.mensaje = '';
    this.isError = false;

    // Endpoint definido en la documentación: POST /auth/forgot-password
    this.http.post('http://localhost:8085/auth/forgot-password', { email: this.email }).subscribe({
      next: (response: any) => {
        this.mensaje = response.mensaje;
        this.loading = false;
        this.isError = false;
      },
      error: (error) => {
        // Manejo de errores según la tabla de la documentación
        this.mensaje = error.error?.error || 'Error al enviar la solicitud';
        this.isError = true;
        this.loading = false;
      }
    });
  }
}