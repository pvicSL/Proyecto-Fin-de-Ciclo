export interface LoginResponse {
  token: string;
  message: string;
  // Campos opcionales por si tu compañera los añade luego
  id: number;
  email: string;
  nombre: string;
  rol: string;
  
}