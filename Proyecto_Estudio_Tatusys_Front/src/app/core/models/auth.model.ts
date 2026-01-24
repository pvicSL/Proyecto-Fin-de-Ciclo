export interface AuthResponse {
  token: string;
  id: number;
  email: string;
  nombre: string;
  rol: 'ADMIN' | 'TRABAJADOR';
}