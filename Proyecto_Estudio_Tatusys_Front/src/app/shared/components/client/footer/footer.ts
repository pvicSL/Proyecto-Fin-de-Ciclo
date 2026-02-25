import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './footer.html',
  styleUrl: './footer.css',
})
export class FooterComponent {

  // El año presente en el footer se actualiza con una variable
  currentYear: number = new Date().getFullYear();

  ngOnInit(): void {
    // Log de consola para verificar que el footer se carga y el año se calcula bien
    console.log(`[DEBUG] FooterComponent inicializado. Año actual: ${this.currentYear}`);
  }
}
