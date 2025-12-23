import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class BookingService {
    private apiUrl = 'http://localhost:8085/api/citas/crear-cita';

    constructor(private http: HttpClient) { }

    reservarCita(datosCita: any): Observable<any> {
        return this.http.post(this.apiUrl, datosCita);
    }
}