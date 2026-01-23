import { ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AppointmentService } from '../../../../core/services/appointment.service';
import { ActivatedRoute } from '@angular/router';
import { AppointmentAdminDTO } from '../../../../core/models/appointment-admin.model';
import { FormatoHorasPipe } from '../../../../pipes/formato-horas-pipe';
import { PricesService } from '../../../../core/services/prices.service';
import { AppointmentDTO } from '../../../../core/models/appointment.model';
import { PricesAdminDTO } from '../../../../core/models/prices-admin.model';
import { forkJoin } from 'rxjs';
import { UploadService } from '../../../../core/services/upload.service';

@Component({
  selector: 'app-appointment-modify',
  imports: [CommonModule, FormsModule, ],
  templateUrl: './appointment-modify.html',
  styleUrls: ['./appointment-modify.css'],
  encapsulation: ViewEncapsulation.None
})
export class AppointmentModify implements OnInit {

  cita!: AppointmentAdminDTO;
  citaDTO!: AppointmentDTO;
  precio!: PricesAdminDTO;
  citaOriginal!: AppointmentAdminDTO; // <-- Importante: El respaldo
  tarifasBBDD: any = {};
  duracionEstimada: number = 0;
  

  

  constructor(
    private appointmentService: AppointmentService,
    private route: ActivatedRoute,
    private priceService: PricesService,
    private uploadService: UploadService,
    private cdr: ChangeDetectorRef // Inyectamos el detector de cambios
  ) {}

  actualizarCalculos() {
  if (!this.cita || !this.tarifasBBDD) return;

  // Función de búsqueda segura
  const obtenerPrecio = (seleccion: string | undefined) => {
    if (!seleccion) return 0;
    return this.tarifasBBDD[seleccion.toUpperCase()] || 0;
  };

  // 1. Obtenemos cada precio individual de tu JSON
  const pTipo = obtenerPrecio(this.cita.tipo);
  const pZona = obtenerPrecio(this.cita.zona);
  const pTamanio = obtenerPrecio(this.cita.tamanio);
  const pDetalle = obtenerPrecio(this.cita.detalle);
  const pEstilo = obtenerPrecio(this.cita.estilo);
  const pColor = obtenerPrecio(this.cita.coloracion); // Buscará 'COLOR' o 'NEGRO'
  const precioBase = this.precio?.precioBase || 0;


  // 2. Sumamos todo
  // Si tienes un precio mínimo de entrada, súmalo aquí (ej: + 40)
  this.cita.precioSinIva = precioBase + pTipo + pZona + pTamanio + pDetalle + pEstilo + pColor;

  // 3. Impuestos y Total
  this.cita.iva = this.cita.precioSinIva * 0.21;
  this.cita.precioFinal = this.cita.precioSinIva + this.cita.iva;

  console.log("Suma realizada con éxito:", this.cita.precioFinal);

  // SINCRONIZACIÓN: Pasamos los cambios de 'cita' a 'citaDTO'
  // Esto es necesario porque el HTML modifica 'this.cita', pero el servicio usa 'this.citaDTO'
    Object.assign(this.citaDTO, {
    tipo: this.cita.tipo,
    zona: this.cita.zona,
    tamanio: this.cita.tamanio,
    detalle: this.cita.detalle,
    coloracion: this.cita.coloracion,
    estilo: this.cita.estilo
  });

  this.appointmentService.calcularDuracion(this.citaDTO).subscribe({
    next: (minutos) => {
      this.duracionEstimada = minutos;
      this.cdr.detectChanges();
    },
    error: (err) => console.error('Error al calcular duración:', err)
  });
}

  ngOnInit(): void {
    const id = this.route.snapshot.params['id'];
    
    forkJoin({
      admin: this.appointmentService.getAppointmentDetails(id),
      precios: this.priceService.getPrices(id),
      base: this.appointmentService.getAppointment(id),
      todosLosPrecios: this.priceService.getAllPrices()
    }).subscribe({
      next: (respuestas) => {

        this.precio = respuestas.precios;

        this.citaDTO = respuestas.base;
        // 1. Extraemos el objeto (por si viene como array)
        const datosAdmin = Array.isArray(respuestas.admin) ? respuestas.admin[0] : respuestas.admin;
        
        // 2. Asignamos a la vista
        this.cita = datosAdmin;
        
        

        if (this.cita.imagenRef1) this.cita.imagenRef1 = this.uploadService.getImagenUrl(this.cita.imagenRef1);
        if (this.cita.imagenRef2) this.cita.imagenRef2 = this.uploadService.getImagenUrl(this.cita.imagenRef2);
        if (this.cita.imagenRef3) this.cita.imagenRef3 = this.uploadService.getImagenUrl(this.cita.imagenRef3);

        // En tu subscribe del ngOnInit
        respuestas.todosLosPrecios.forEach(p => {
          // Verificamos 'valor' y 'precioAdicional' que son los nombres de tu JSON
          if (p && p.valor) {
            const llave = p.valor.trim().toUpperCase();
            this.tarifasBBDD[llave] = p.precioAdicional; // Guardamos el precio adicional
          }
        });

        // Agregamos manualmente el SERVICIO_BASE si quieres que sume desde ahí
        this.cita.precioSinIva = this.tarifasBBDD['SERVICIO_BASE'] || 0;
        // Transformamos los nombres de archivo en URLs completas para el HTML
        

        

        // 3. CREAMOS LA COPIA DE SEGURIDAD
        this.citaOriginal = JSON.parse(JSON.stringify(datosAdmin));
        setTimeout(() => this.actualizarCalculos());
      },
      error: (err) => console.error('Error al cargar:', err)
    });
  }



  // Si pulsa la 'X' o cerrar sin guardar
  cerrarDetalle() {
    // Restauramos el original antes de volver atrás para limpiar la memoria
    this.cita = JSON.parse(JSON.stringify(this.citaOriginal));
    window.history.back();
  }

  guardarPresupuesto() {
  if (this.cita && this.cita.idCita) {
    // Asegúrate de que los comentarios del textarea se guarden en el campo correcto
    console.log("Datos a enviar:", this.cita);

    this.appointmentService.updateAppointmentDetails(this.cita.idCita, this.cita)
      .subscribe({
        next: (res) => {
          alert('¡Presupuesto actualizado correctamente!');
          window.history.back();
        },
        error: (err) => {
          console.error("Error del servidor:", err);
          alert('Error al guardar: ' + (err.error?.message || 'Revisa la consola'));
        }
      });
  }
}

getBadgeClass(estado: string | undefined): string {
  
  switch (this.cita.estadoPresupuesto) {
    case 'PENDIENTE': return 'badge bg-warning';
    case 'CONFIRMADO': return 'badge bg-success';
    case 'RECHAZADO': return 'badge bg-danger';
    case 'ENVIADO': return 'badge bg-info';
    default: return 'bg-primary';
  }
}





  
} 
