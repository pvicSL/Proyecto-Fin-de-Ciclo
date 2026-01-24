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
  if (!this.cita || !this.citaOriginal || !this.tarifasBBDD || !this.precio) return;

  const obtenerTarifa = (val: string | undefined) => {
    if (!val) return 0;
    return Number(this.tarifasBBDD[val.toUpperCase()] || 0);
  };

  // 1. ¿Tiene la cita un precio guardado ya?
  const precioGuardadoEnBD = Number(this.citaOriginal.precioSinIva) || 0;

  if (precioGuardadoEnBD > 0) {
    // CASO: YA EXISTE UN PRECIO. Aplicamos variaciones sobre ese precio pactado.
    let nuevoPrecioSinIva = precioGuardadoEnBD;
    const campos = ['tipo', 'zona', 'tamanio', 'detalle', 'estilo', 'coloracion'];

    campos.forEach(campo => {
      const valorActual = (this.cita as any)[campo];
      const valorOriginal = (this.citaOriginal as any)[campo];

      if (valorActual !== valorOriginal) {
        const pViejo = obtenerTarifa(valorOriginal);
        const pNuevo = obtenerTarifa(valorActual);
        nuevoPrecioSinIva = nuevoPrecioSinIva - pViejo + pNuevo;
      }
    });
    this.cita.precioSinIva = nuevoPrecioSinIva;
  } else {
    // CASO: CITA NUEVA (precio 0). Calculamos el total de cero.
    const base = Number(this.precio.precioBase) || 0;
    const extras = obtenerTarifa(this.cita.tipo) + obtenerTarifa(this.cita.zona) + 
                   obtenerTarifa(this.cita.tamanio) + obtenerTarifa(this.cita.detalle) + 
                   obtenerTarifa(this.cita.estilo) + obtenerTarifa(this.cita.coloracion);
    this.cita.precioSinIva = base + extras;
  }

  // 2. Impuestos y Total
  this.cita.iva = this.cita.precioSinIva * 0.21;
  this.cita.precioFinal = this.cita.precioSinIva + this.cita.iva;

  this.sincronizarYCalcularDuracion();
}

private sincronizarYCalcularDuracion() {
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
      // 1. Asignaciones básicas
      this.precio = respuestas.precios;
      this.citaDTO = respuestas.base;
      const datosAdmin = Array.isArray(respuestas.admin) ? respuestas.admin[0] : respuestas.admin;
      this.cita = datosAdmin;

      // 2. Mapeo de tarifas del catálogo
      respuestas.todosLosPrecios.forEach(p => {
        if (p && p.valor) {
          const llave = p.valor.trim().toUpperCase();
          this.tarifasBBDD[llave] = p.precioAdicional;
        }
      });

      // 3. URLs de imágenes
      if (this.cita.imagenRef1) this.cita.imagenRef1 = this.uploadService.getImagenUrl(this.cita.imagenRef1);
      if (this.cita.imagenRef2) this.cita.imagenRef2 = this.uploadService.getImagenUrl(this.cita.imagenRef2);
      if (this.cita.imagenRef3) this.cita.imagenRef3 = this.uploadService.getImagenUrl(this.cita.imagenRef3);

      // --- ¡OJO AQUÍ! ---
      // HE QUITADO la línea que ponía precioSinIva = SERVICIO_BASE.
      // Dejamos que 'this.cita' conserve el precio que trae del servidor.

      // 4. CREAMOS LA COPIA DE SEGURIDAD (con el precio real de la BD)
      this.citaOriginal = JSON.parse(JSON.stringify(this.cita));
      
      // 5. Ejecutamos el cálculo inicial
      this.actualizarCalculos();
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
    case 'ACEPTADO': return 'badge bg-success';
    case 'RECHAZADO': return 'badge bg-danger';
    case 'GENERADO': return 'badge bg-info';
    default: return 'bg-primary';
  }
}





  
} 
