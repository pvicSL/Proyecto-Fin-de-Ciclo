import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UploadService {

  private readonly API_URL = 'http://localhost:8085/api'; // Tu URL base

  getImagenUrl(filename: string): string {
  if (!filename) return '';
  
  // Extraemos solo el nombre por si acaso viene una ruta completa
  const soloNombre = filename.split('/').pop() || filename;
  
  // Codificamos para que los espacios y puntos no den error 400
  return `${this.API_URL}/imagen-optimizada/${encodeURIComponent(soloNombre)}`;
}
  
}
