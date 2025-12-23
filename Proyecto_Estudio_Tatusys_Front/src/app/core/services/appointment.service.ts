import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { AppointmentDTO } from '../models/appointment.model';

@Injectable({
    providedIn: 'root'
})
export class AppointmentService {

    // Asumiendo que tu backend corre aquí
    private apiUrl = 'http://localhost:8085/api/citas';

    constructor(private http: HttpClient) { }

    // 1. MÉTODO PARA CREAR LA CITA (ESTE ES EL QUE NECESITAMOS AHORA)
    createAppointment(cita: any): Observable<any> {
        // Apunta al endpoint exacto de tu Controller
        return this.http.post(`${this.apiUrl}/crear-cita`, cita);
    }

    // 1. OBTENER CITA POR ID (Tu referencia)
    getAppointmentByRef(id: string): Observable<AppointmentDTO> {
        return this.http.get<AppointmentDTO>(`${this.apiUrl}/${id}`);
    }

    // 2. MODIFICAR FECHA (Solo enviamos los cambios necesarios o el DTO entero actualizado)
    updateAppointmentDate(id: number, newDate: string, newTime: string): Observable<AppointmentDTO> {
        // Backend espera un PUT. Puedes enviar solo fecha/hora o el objeto.
        // Aquí simplificamos enviando un objeto parcial o usando un endpoint específico si lo creas.
        return this.http.put<AppointmentDTO>(`${this.apiUrl}/${id}`, { fecha: newDate, hora: newTime });
    }

    // 3. CANCELAR (Cambiar estatus a CANCELADO o RECHAZADO)
    cancelAppointment(id: number): Observable<any> {
        // Si usas borrado lógico (cambiar estado):
        return this.http.patch(`${this.apiUrl}/${id}/cancelar`, {});
        // Si usas borrado físico (DELETE):
        // return this.http.delete(`${this.apiUrl}/${id}`);
    }

    // 4. BUSCAR HUECOS (Simulación inteligente basada en duración)
    // En el futuro, esto llamará a: GET /citas/huecos-libres?duracion=120
    getAvailableSlots(duration: number): Observable<string[]> {
        // Mockup temporal para que no te falle la compilación
        return of([]);
    }
}