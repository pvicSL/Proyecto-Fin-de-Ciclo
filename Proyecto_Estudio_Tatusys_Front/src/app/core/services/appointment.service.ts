import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppointmentDTO } from '../models/appointment.model';
import { AppointmentAdminDTO } from '../models/appointment-admin.model';

@Injectable({
    providedIn: 'root'
})
export class AppointmentService {

    // URL base de la API
    private apiUrl = 'http://localhost:8085/api/citas';

    // Inyección moderna de dependencias (opcional, pero recomendada)
    private http = inject(HttpClient);

    // ==========================================
    // 1. CREACIÓN Y CONSULTA DE DURACIÓN
    // ==========================================

    createAppointment(formData: FormData): Observable<any> {
        return this.http.post(`${this.apiUrl}/crear-cita`, formData);
    }

    calculateDuration(criteria: { tamanio: string, detalle: string, coloracion: string }): Observable<number> {
        return this.http.post<number>(`${this.apiUrl}/calcular-duracion`, criteria);
    }

    // ==========================================
    // 2. BÚSQUEDA SEGURA (Por Referencia + Email)
    // ==========================================

    // NOTA: He eliminado 'getAppointmentByRef(id)' porque no es seguro usar IDs directos.

    /**
     * Busca una cita para mostrarla en el modificador.
     * Requiere Referencia y Email para validar la propiedad.
     */
    getAppointmentByLocator(reference: string, email: string): Observable<any> {
        const params = new HttpParams()
            .set('ref', reference)
            .set('email', email);

        return this.http.get(`${this.apiUrl}/buscar`, { params });
    }

    // ==========================================
    // 3. MODIFICACIÓN Y CANCELACIÓN (¡CAMBIO IMPORTANTE!)
    // ==========================================

    /**
     * Modifica la fecha de una cita usando la REFERENCIA como llave.
     * OJO: Tu Backend debe tener un endpoint que acepte esto.
     */
    updateAppointmentDate(referencia: string, email: string, fecha: string, hora: string): Observable<any> {
        console.log("Enviando modificación para cita:", referencia);

        const payload = {
            referencia: referencia, // Enviamos ref en lugar de ID
            email: email,          // Enviamos email por seguridad extra
            fecha: fecha,
            hora: hora
        };

        // CAMBIO: La ruta ya no es /actualizar/{id}, ahora debería ser segura.
        // Sugerencia para backend: PUT /api/citas/actualizar-seguro
        return this.http.put(`${this.apiUrl}/modificar-conreferencia`, payload);
    }

    /**
     * Cancela una cita usando REFERENCIA y EMAIL.
     * Ya no usamos el ID numérico en la URL para evitar borrados accidentales/maliciosos.
     */
    cancelAppointment(referencia: string, email: string): Observable<any> {
        const params = new HttpParams()
            .set('ref', referencia)
            .set('email', email);

        // CAMBIO: Delete ahora envía parámetros, no un ID en el path.
        // Sugerencia para backend: DELETE /api/citas/eliminar-seguro?ref=...&email=...
        return this.http.delete(`${this.apiUrl}/cancelar-conreferencia`, { params, responseType: 'text' });
    }

    // ==========================================
    // 4. DISPONIBILIDAD (Con Filtros de Empleado)
    // ==========================================

    /**
     * Busca huecos libres.
     * Acepta 'filtros' opcionales para futura implementación de empleados.
     */
    getAvailableSlots(durationMinutes: number, filtros?: { tipo: string, estilo: string }): Observable<any> {

        // 1. Configuración básica (LO QUE FUNCIONA HOY)
        let url = `${this.apiUrl}/disponibilidad/${durationMinutes}`;
        let params = new HttpParams();

        // 2. Lógica preparada para el futuro (Descomentar cuando Backend esté listo)
        /* if (filtros) {
           // Si hay filtros, cambiamos la estrategia a Query Params
           // url = `${this.apiUrl}/disponibilidad`; 
           // params = params.set('duracion', durationMinutes);
           // params = params.set('tipo', filtros.tipo);
           // params = params.set('estilo', filtros.estilo);
        }
        */

        // Por ahora, si descomentas lo de arriba, pasaría los params.
        // Hoy por hoy, 'params' va vacío y la URL lleva la duración en el path.
        return this.http.get(url, { params });
    }

    // ==========================================
    // 5. MÉTODOS AUXILIARES / ADMIN
    // ==========================================

    getConfirmedAppointments(fecha: string, vista: string): Observable<AppointmentDTO[]> {
        return this.http.get<AppointmentDTO[]>(`${this.apiUrl}/buscar/confirmadas/${fecha}/${vista}`, {
            params: { fecha, vista }
        });
    }

    getRequests(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/presupuesto-pendientes`);
    }

    getGeneratedBudgets(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/presupuestos-generados`);
    }

    getAppointmentDetails(id: number): Observable<AppointmentAdminDTO> {
        return this.http.get<AppointmentAdminDTO>(`${this.apiUrl}/detalles-completos/${id}`);
    }

    updateAppointmentDetails(id: number, appointment: AppointmentAdminDTO): Observable<any> {
        const url = `${this.apiUrl}/detalles-completos-modificar/${id}`;
        return this.http.put<AppointmentAdminDTO>(url, appointment);
    }

    
    calcularDuracion(datos: AppointmentDTO): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/calcular-duracion/`, datos);
    }

    getAppointment(id: number): Observable<AppointmentDTO> {
        return this.http.get<AppointmentDTO>(`${this.apiUrl}/${id}`);
    }

    simularPresupuesto(appointment: AppointmentAdminDTO): Observable<AppointmentAdminDTO> {
    // Eliminamos la barra final si tu @PostMapping no la tiene (es mejor ser exactos)
    return this.http.post<AppointmentAdminDTO>(`${this.apiUrl}/simular`, appointment);
}
    
}
