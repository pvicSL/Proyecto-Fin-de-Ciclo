import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.css',
})
export class FooterComponent {

  // El año presente en el footer se actualiza con una variable
  currentYear: number = new Date().getFullYear();
}
