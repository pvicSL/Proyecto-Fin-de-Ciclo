import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Precio } from '../models/prices-admin.model';

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

    getAllPrices(): Observable<any[]> {
      return this.http.get<any[]>(`${this.apiUrl}`);
    }

    // @PostMapping
  createPrice(precio: Precio): Observable<Precio> {
    return this.http.post<Precio>(this.apiUrl, precio);
  }

  // @PutMapping("/{idPrecio}")
  updatePrice(id: number, precio: Precio): Observable<Precio> {
    return this.http.put<Precio>(`${this.apiUrl}/${id}`, precio);
  }

  // @DeleteMapping("/{idPrecio}")
  deletePrice(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // @PutMapping("/actualizar-base")
  updateBasePrice(nuevoPrecio: number): Observable<any> {
    return this.http.put(`${this.apiUrl}/actualizar-base`, nuevoPrecio);
  }
}
