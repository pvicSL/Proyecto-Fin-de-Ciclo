import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {


  private baseUrl = 'http://localhost:8085/api/pago';

  constructor(private http: HttpClient) { }

  validarToken(token: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/validar/${token}`);
  }

  procesarPago(datosPago: { token: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/procesar`, datosPago);
  }
}