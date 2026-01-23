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

    /*COMENTO ESTE MÉTODO PORQUE PARECE QUE YA NO LO USO, PERO NO
    VOY A BORRARLO HASTA COMPROBARLO EN FIRME
    calculateDuration(criteria: { tamanio: string, detalle: string, coloracion: string }): Observable<number> {
        return this.http.post<number>(`${this.apiUrl}/calcular-duracion`, criteria);
    }
    */

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

    //Modifica la fecha de una cita usando la REFERENCIA como llave.
    updateAppointmentDate(referencia: string, email: string, nuevaFecha: string, nuevaHora: string): Observable<any> {
        // El backend espera un objeto JSON (CitaModificacionDTO)
        const body = {
            referencia: referencia,
            email: email,
            nuevaFecha: nuevaFecha,
            nuevaHora: nuevaHora + ":00" // Aseguramos formato HH:mm:ss si hace falta
        };

        // CORRECCIÓN: Apuntamos a '/modificar-conreferencia'
        return this.http.put(`${this.apiUrl}/modificar-conreferencia`, body);
    }

    /**
     * Cancela una cita usando REFERENCIA y EMAIL.
     * Ya no usamos el ID numérico en la URL para evitar borrados accidentales/maliciosos.
     */
    cancelAppointment(referencia: string, email: string): Observable<any> {
        const params = new HttpParams()
            .set('ref', referencia)
            .set('email', email);

        // AÑADIR: responseType: 'text' as 'json'
        // Esto fuerza a Angular a tratar la respuesta como un string simple
        return this.http.delete(`${this.apiUrl}/cancelar-conreferencia`, {
            params,
            responseType: 'text' as 'json'
        });
    }

    // ==========================================
    // 4. DISPONIBILIDAD (Con Filtros de Empleado)
    // ==========================================

    // Nuevo método para obtener duración y trabajador
    calculatePreBookingData(criterios: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/calculo-previo`, criterios);
    }

    /**
   * Busca huecos libres para una duración específica y un trabajador concreto.
   * Se llama DESPUÉS de haber calculado la duración y asignado el trabajador.
   * * @param durationMinutes Duración en minutos (ej: 120)
   * @param workerId ID del trabajador asignado (ej: 3)
   */
    /**
   * Busca huecos libres.
   * workerId es opcional (?) para mantener compatibilidad con el modificador de citas antiguo.
   */
    getAvailableSlots(duracion: number, workerId?: number): Observable<any> {
        let params = new HttpParams().set('duracion', duracion.toString());

        if (workerId) {
            params = params.set('idTrabajador', workerId.toString());
        }

        // CORRECCIÓN: La ruta en tu Back es '/huecos-disponibles', no '/disponibilidad'
        return this.http.get(`${this.apiUrl}/huecos-disponibles`, { params });
    }

    /*
      METODO ANTERIOR COMENTADO
       * Busca huecos libres.
       * Acepta 'filtros' opcionales para futura implementación de empleados.
      getAvailableSlots(durationMinutes: number, filtros?: { tipo: string, estilo: string }): Observable<any> {
  
          // 1. Configuración básica (LO QUE FUNCIONA HOY)
          let url = `${this.apiUrl}/disponibilidad/${durationMinutes}`;
          let params = new HttpParams();
  
          // 2. Lógica preparada para el futuro (Descomentar cuando Backend esté listo)
           if (filtros) {
             // Si hay filtros, cambiamos la estrategia a Query Params
             // url = `${this.apiUrl}/disponibilidad`; 
             // params = params.set('duracion', durationMinutes);
             // params = params.set('tipo', filtros.tipo);
             // params = params.set('estilo', filtros.estilo);
          }
          
          // Por ahora, si descomentas lo de arriba, pasaría los params.
          // Hoy por hoy, 'params' va vacío y la URL lleva la duración en el path.
          return this.http.get(url, { params });
      }
      */

    // ==========================================
    // 5. MÉTODOS AUXILIARES / ADMIN
    // ==========================================

    vista: 'dia' | 'semana' = 'dia';
    fechaActual: Date = new Date();
    paginaActual: number = 1;

    getConfirmedAppointments(fecha: string, vista: string): Observable<AppointmentDTO[]> {
        return this.http.get<AppointmentDTO[]>(`${this.apiUrl}/buscar/confirmadas/${fecha}/${vista}`, {
            params: { fecha, vista }
        });
    }

    getNumberConfirmedAppointments(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/confirmadas`);
    }

    getRequests(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/presupuesto-pendientes`);
    }

    getGeneratedBudgets(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/presupuesto-generados`);
    }

    getAceptedBudgets(): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/buscar/presupuesto-aceptados`);
    }

    getAppointmentDetails(id: number): Observable<AppointmentAdminDTO> {
        return this.http.get<AppointmentAdminDTO>(`${this.apiUrl}/detalles-completos/${id}`);
    }

    updateAppointmentDetails(id: number, appointment: AppointmentAdminDTO): Observable<any> {
        const url = `${this.apiUrl}/detalles-completos-modificar/${id}`;
        return this.http.put<AppointmentAdminDTO>(url, appointment);
    }


    deleteAppointment(id: number): Observable<any> {
        return this.http.put<any>(`${this.apiUrl}/${id}/presupuesto/rechazar`,{});
        }



    calcularDuracion(datos: AppointmentDTO): Observable<number> {
        return this.http.post<number>(`${this.apiUrl}/calcular-duracion`, datos);
    }

    /* MÉTODO COMENTADO, EN PRINCIPIO YA NO SE USA, PERO ANTES DE ELIMINARLO
    DEL TODO, LO DEJO ASÍ PARA HACER COMPROBACIONES
    calcularDuracion(datos: AppointmentDTO): Observable<number> {
            return this.http.post<number>(`${this.apiUrl}/calcular-duracion/`, datos);
        }*/


    getAppointment(id: number): Observable<AppointmentDTO> {
        return this.http.get<AppointmentDTO>(`${this.apiUrl}/${id}`);
    }



}
