import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PricesService {
  // URL base de la API
    private apiUrl = 'http://localhost:8085/api/precios';

    // Inyección moderna de dependencias (opcional, pero recomendada)
    private http = inject(HttpClient);

    getPrices(idCita: string): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/${idCita}`);
    }
}
