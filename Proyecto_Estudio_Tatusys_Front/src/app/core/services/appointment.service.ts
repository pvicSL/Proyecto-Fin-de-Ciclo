import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { AppointmentDTO } from '../models/appointment.model';
import { AppointmentAdminDTO } from '../models/appointment-admin.model';




@Injectable({
    providedIn: 'root'
})
export class AppointmentService {

    // URL correspondiente a las citas
    private apiUrl = 'http://localhost:8085/api/citas';

    constructor(private http: HttpClient) { }

    // 1. MÉTODO PARA CREAR LA CITA
    createAppointment(formData: FormData): Observable<any> {
        // Nota: NO establecemos 'Content-Type' manualmente. 
        // Angular lo detecta y lo pone como 'multipart/form-data' automáticamente.
        return this.http.post(`${this.apiUrl}/crear-cita`, formData);
    }

    // 2. OBTENER CITA POR ID (se debe cambiar por una clave más compleja, no es seguro que usemos la id numérica sin más)
    getAppointmentByRef(id: string): Observable<AppointmentDTO> {
        return this.http.get<AppointmentDTO>(`${this.apiUrl}/${id}`);
    }


    /**
   * Busca una cita por su código de referencia (Localizador) y el email del cliente via Query Params.
   * Backend espera: /api/citas/buscar?ref=XXXX&email=yyyy@mail.com
   */
    getAppointmentByLocator(reference: string, email: string): Observable<any> {
        // Usamos params para codificar correctamente el email y la referencia en la URL
        return this.http.get(`${this.apiUrl}/buscar`, {
            params: {
                ref: reference,
                email: email
            }
        });
    }

    // 3. MODIFICAR FECHA (Solo enviamos los cambios necesarios: el id, que es el mismo, y la fecha y la hora)
    updateAppointmentDate(id: number, fecha: string, hora: string): Observable<any> {

        // 1. Verificamos por consola que el ID llega bien (IMPORTANTE)
        console.log("Enviando modificación de horario para cita con ID:", id);

        const payload = {
            idCita: id,
            fecha: fecha,
            hora: hora
        };

        // 2. Usa el PUT y la ruta con el id de la cita
        // (PUEDE QUE SEA NECESARIO MODIFICAR ESTO CUANDO SE CAMBIE LA CLAVE PARA RECUPERAR UNA CITA)
        return this.http.put(`${this.apiUrl}/actualizar/${id}`, payload);
    }

    // 3. MÉTODO DE CANCELAR UNA CITA (Cambia el estatus en la BBDD)
    cancelAppointment(id: number): Observable<any> {
        // Importante: se usa responseType: 'text' porque el back devuelve un String plano
        return this.http.delete(`${this.apiUrl}/eliminar/${id}`, { responseType: 'text' });
    }

    // 4. BÚSQUEDA DE HUECOS EN LA AGENDA DEL ESTUDIO
    // se deberá añadir la id de empleado para la búsqueda, no solo los minutos.
    getAvailableSlots(durationMinutes: number): Observable<any> {
        // Devuelve un objeto JSON tipo: { "2026-12-20": ["10:00", "10:30"], ... }
        // Busca la disponibilidad por minutos.
        return this.http.get(`${this.apiUrl}/disponibilidad/${durationMinutes}`);
    }

    // Petición GET al endpoint específico de confirmadas
    getConfirmedAppointments(fecha: string, vista: string): Observable<AppointmentDTO[]> {
    // Enviamos la fecha y la vista como parámetros: ?fecha=2026-01-05&vista=dia
    return this.http.get<AppointmentDTO[]>(`${this.apiUrl}/buscar/confirmadas/${fecha}/${vista}`, {
        params: { fecha, vista }
    });
    
    }

    getRequests(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/pendientes`);
    }

    getDetalleCita(id: number): Observable<AppointmentAdminDTO> {
    return this.http.get<AppointmentAdminDTO>(`${this.apiUrl}/detalle/${id}`);
    }

}
