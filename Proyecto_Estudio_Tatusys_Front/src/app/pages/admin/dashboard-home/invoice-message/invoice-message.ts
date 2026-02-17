import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-invoice-message',
  imports: [],
  templateUrl: './invoice-message.html',
  styleUrl: './invoice-message.css',
})
export class InvoiceMessage {

  constructor(private router: Router) {}

  irAlListado() {
    // Te redirige al listado de citas (ajusta la ruta según tu app-routing.module)
    this.router.navigate(['/admin/citas']); 
  }
}
