import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class StaffService {

  // URL base de la API
    private apiUrl = 'http://localhost:8085/api/admin';

    // Inyección moderna de dependencias (opcional, pero recomendada)
    private http = inject(HttpClient);

    getStaff(): Observable<any> {
            return this.http.get<any>(`${this.apiUrl}/trabajadores`);
        }
  
}
