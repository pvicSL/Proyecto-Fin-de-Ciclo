import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StaffAdminDTO } from '../models/staff-admin.model';

@Injectable({
  providedIn: 'root',
})
export class StaffService {

  // URL base de la API
    private apiUrl = 'http://localhost:8085/api/admin';

    // Inyección moderna de dependencias (opcional, pero recomendada)
    private http = inject(HttpClient);

    getStaff(): Observable<any> {
      return this.http.get<any>(`${this.apiUrl}/trabajadores/todos`);
    }

    getStaffById(id: number): Observable<any> {
      return this.http.get<any>(`${this.apiUrl}/trabajadores/${id}`);
    }

    updateStaff(id: number, staff: StaffAdminDTO): Observable<any>{
      return this.http.put<StaffAdminDTO>(`${this.apiUrl}/trabajadores/${id}`,staff);
    }

    deleteStaff(trabajadorId: number): Observable<any> {
      return this.http.delete(`${this.apiUrl}/trabajadores/${trabajadorId}`);
    }

    createStaff(staff: StaffAdminDTO): Observable<any>{
      return this.http.post(`${this.apiUrl}/trabajador-alta`, staff)
    }
  
}
