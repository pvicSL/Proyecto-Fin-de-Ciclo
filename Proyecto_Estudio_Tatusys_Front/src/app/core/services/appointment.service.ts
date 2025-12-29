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
    updateAppointmentDate(id: number, fecha: string, hora: string): Observable<any> {

        // 1. Verificamos por consola que el ID llega bien (IMPORTANTE)
        console.log("Enviando actualización para ID:", id);

        const payload = {
            idCita: id,
            fecha: fecha,
            hora: hora
        };

        // 2. CORRECCIÓN CLAVE: Añadimos "/${id}" al final de la URL
        // Fíjate en la barra inclinada antes del dólar
        return this.http.put(`${this.apiUrl}/actualizar/${id}`, payload);
    }

    // 3. CANCELAR (Cambiar estatus a CANCELADO o RECHAZADO)
    cancelAppointment(id: number): Observable<any> {
        // Importante: responseType: 'text' porque el back devuelve un String plano, no un JSON
        return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
    }

    // 4. BUSCAR HUECOS REALES (Conectado al Back)
    getAvailableSlots(durationMinutes: number): Observable<any> {
        // Esto devuelve un objeto JSON tipo: { "2026-12-20": ["10:00", "10:30"], ... }
        return this.http.get(`${this.apiUrl}/disponibilidad/${durationMinutes}`);
    }
}