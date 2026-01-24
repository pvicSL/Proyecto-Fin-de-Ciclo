import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PricesService } from '../../../../core/services/prices.service';
import { CategoriaEnum, Precio } from '../../../../core/models/prices-admin.model';

@Component({
  selector: 'app-services-settings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './services-settings.html',
  styleUrl: './services-settings.css',
})
export class ServicesSettings implements OnInit {
  // Estructura para la vista
  serviciosConfig: Record<string, Precio[]> = {
    COLORACION: [],
    DETALLE: [],
    ESTILO: [],
    TAMANIO: [],
    TIPO: [],
    ZONA: []
  };

  precioBaseGlobal: number = 0;
  
  // Control de cambios
  serviciosOriginales: Map<number, string> = new Map(); // Para comparar valores originales
  cambiosPendientes: Set<number> = new Set(); 
  hayCambios: boolean = false;

  constructor(
    private priceService: PricesService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos() {
    this.priceService.getAllPrices().subscribe(precios => {
      // Limpiar estructura
      Object.keys(this.serviciosConfig).forEach(k => this.serviciosConfig[k] = []);
      this.serviciosOriginales.clear();
      this.cambiosPendientes.clear();
      this.hayCambios = false;

      precios.forEach(p => {
        if (p.valor === 'SERVICIO_BASE') {
          this.precioBaseGlobal = p.precioAdicional;
        } else if (this.serviciosConfig[p.categoria]) {
          this.serviciosConfig[p.categoria].push(p);
        }
        
        // Guardamos una "foto" del estado original para comparar
        if (p.idPrecio) {
          this.serviciosOriginales.set(p.idPrecio, JSON.stringify(p));
        }
      });
      this.cdr.detectChanges();
    });
  }

  // Se ejecuta en el (input) de los campos del HTML
  registrarCambio(item: Precio) {
    if (!item.idPrecio) return;

    const original = this.serviciosOriginales.get(item.idPrecio);
    const actual = JSON.stringify(item);

    if (original !== actual) {
      this.cambiosPendientes.add(item.idPrecio);
    } else {
      this.cambiosPendientes.delete(item.idPrecio);
    }

    this.hayCambios = this.cambiosPendientes.size > 0;
  }

  confirmarYGuardar() {
    const listaCambios = this.obtenerItemsCambiados();
    
    if (listaCambios.length === 0) return;

    const mensaje = listaCambios.map(i => `• ${i.valor}: ${i.precioAdicional}€`).join('\n');
    
    if (confirm(`Se van a guardar los siguientes cambios:\n\n${mensaje}\n\n¿Estás seguro?`)) {
      // Guardamos todos los cambios uno por uno
      let completados = 0;
      listaCambios.forEach(item => {
        this.priceService.updatePrice(item.idPrecio!, item).subscribe({
          next: () => {
            completados++;
            if (completados === listaCambios.length) {
              alert('Todos los cambios se han guardado correctamente.');
              this.cargarDatos(); // Recarga para limpiar el estado de cambios
            }
          }
        });
      });
    }
  }

  obtenerItemsCambiados(): Precio[] {
    return Object.values(this.serviciosConfig)
      .flat()
      .filter(item => this.cambiosPendientes.has(item.idPrecio!));
  }

  // Operaciones inmediatas (POST y DELETE suelen ser inmediatas)
  crearServicio(categoria: string) {
    const nombre = prompt(`Nombre del nuevo ${categoria}:`);
    if (!nombre) return;

    const nuevoPrecio: Precio = {
      categoria: categoria as CategoriaEnum,
      valor: nombre.toUpperCase(),
      precioAdicional: 0,
      activo: true
    };

    this.priceService.createPrice(nuevoPrecio).subscribe(() => this.cargarDatos());
  }

  borrar(id?: number) {
    if (id && confirm('¿Eliminar servicio permanentemente de la base de datos?')) {
      this.priceService.deletePrice(id).subscribe(() => this.cargarDatos());
    }
  }

  guardarPrecioBase() {
    this.priceService.updateBasePrice(this.precioBaseGlobal).subscribe({
      next: () => alert('Precio base actualizado con éxito'),
      error: (err) => console.error('Error al actualizar precio base', err)
    });
  }

  // Método para el Guard de navegación
  tieneCambiosSinGuardar(): boolean {
    return this.hayCambios;
  }
}