import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PresupuestoService {

  private apiUrl = 'http://localhost:8085/api'; // Ajusta URL

  constructor(private http: HttpClient) { }

  obtenerPresupuestoPorCita(idCita: number): Observable<any> {
    // Esta ruta debe coincidir con @GetMapping("/por-cita/{idCita}") en Java
    return this.http.get(`${this.apiUrl}/buscar-presupuesto/${idCita}`);
  }

  actualizarPresupuestoConExtra(idCita: number, presupuesto: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/actualizar-generar/${idCita}`, presupuesto);
    }

}