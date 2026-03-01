import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Location, CommonModule } from '@angular/common';
import { forkJoin } from 'rxjs';
import { AppointmentService } from '../../../../core/services/appointment.service';
import { PricesService } from '../../../../core/services/prices.service';
import { UploadService } from '../../../../core/services/upload.service';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-invoice-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './invoice-details.html',
  styleUrl: './invoice-details.css'
})
export class InvoiceDetails implements OnInit {
  // Variables para almacenar toda la información necesaria
  cita!: any; 
  precio!: any;
  citaDTO!: any;
  cargando = true;

  constructor(
    private appointmentService: AppointmentService, 
    private route: ActivatedRoute,
    private location: Location,
    private priceService: PricesService,
    private uploadService: UploadService,
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    
    // Ejecutamos las 3 peticiones en paralelo
    forkJoin({
      admin: this.appointmentService.getAppointmentDetails(id),
      precios: this.priceService.getPrices(id),
      base: this.appointmentService.getAppointment(id)
    }).subscribe({
      next: (respuestas) => {
        const datosAdmin = Array.isArray(respuestas.admin) ? respuestas.admin[0] : respuestas.admin;
        
        this.cita = datosAdmin;
        this.precio = respuestas.precios;
        this.citaDTO = respuestas.base;
        this.cargando = false;

        console.log('Factura lista con datos completos:', { cita: this.cita, precio: this.precio });
      },
      error: (err) => {
        console.error('Error al obtener datos de la factura:', err);
        this.cargando = false;
      }
    });
  }

  public descargarPDF(): void {
    const DATA: any = document.getElementById('factura-a4');
    html2canvas(DATA, { scale: 2 }).then((canvas) => {
      const imgWidth = 210;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4');
      pdf.addImage(contentDataURL, 'PNG', 0, 0, imgWidth, imgHeight);
      pdf.save(`Factura_Tatusys_${this.cita.idCita}.pdf`);
    });
  }

  volver(): void {
    this.location.back();
  }

public descargarComoPDF(): void {
    const DATA: any = document.getElementById('seccion-a4');
    
    html2canvas(DATA, { scale: 2 }).then((canvas) => {
      const pdf = new jsPDF('p', 'mm', 'a4');
      
      const margin = 15; // Margen de 15mm (puedes ajustarlo a 10 si prefieres)
      
      // Calculamos el ancho disponible restando márgenes de ambos lados
      const imgWidth = 210 - (margin * 2); 
      
      // Calculamos el alto proporcional para que no se deforme
      const imgHeight = (canvas.height * imgWidth) / canvas.width;
      
      const contentDataURL = canvas.toDataURL('image/png');
      
      // Coordenadas:
      // X = margin (desplaza la imagen a la derecha)
      // Y = margin (desplaza la imagen hacia abajo)
      pdf.addImage(contentDataURL, 'PNG', margin, margin, imgWidth, imgHeight);
      
      pdf.save(`Factura_Tatusys_${this.cita.idCita}.pdf`);
    });
  }
}

