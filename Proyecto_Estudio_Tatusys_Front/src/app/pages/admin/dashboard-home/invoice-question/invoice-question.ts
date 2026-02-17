import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-invoice-question',
  imports: [],
  templateUrl: './invoice-question.html',
  styleUrl: './invoice-question.css',
})
export class InvoiceQuestion {

  constructor(private router: Router) {}

  irAlListado() {
    // Te redirige al listado de citas (ajusta la ruta según tu app-routing.module)
    this.router.navigate(['/admin/citas']); 
  }
}
