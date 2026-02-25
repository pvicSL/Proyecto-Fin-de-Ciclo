import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-terms',
  imports: [RouterLink],
  templateUrl: './terms.html',
  styleUrl: './terms.css',
})
export class TermsComponent implements OnInit {

  // ngOnInit es un "lifecycle hook" (gancho del ciclo de vida) de Angular.
  // Se ejecuta una sola vez, justo después de que Angular haya inicializado 
  // las propiedades enlazadas a datos del componente.
  ngOnInit(): void {
    // Este log permite verificar en la consola del navegador que el 
    // componente se está instanciando y cargando en memoria correctamente.
    console.log('[DEBUG] TermsComponent inicializado correctamente.');
  }

}