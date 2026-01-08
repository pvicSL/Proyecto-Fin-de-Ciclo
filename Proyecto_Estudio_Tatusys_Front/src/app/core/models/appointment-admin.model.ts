export interface AppointmentAdminDTO {
  idServicio: number;
  nombre: string;
  apellido1: string;
  apellido2: string;
  email: string;
  telefono: string;
  dni: string;
  
  // Datos del Servicio y sus precios
  baseServicio: string;
  precioBaseServicio: number;
  tipo: string;
  precioTipo: number;
  zona: string;
  precioZona: number;
  tamanio: string;
  precioTamanio: number;
  detalle: string;
  precioDetalle: number;
  coloracion: string;
  precioColoracion: number;
  estilo: string;
  precioEstilo: number;
  duracionMinutos: number;
  
  comentariosServicio: string;

  imagenRef1: string;
  imagenRef2: string;
  imagenRef3: string;

  // Datos del Presupuesto
  precioBase: number;
  precioExtra: number;
  iva: number;
  precioFinal: number;
  estadoPresupuesto: string;
  fechaPresupuesto: string;
  comentarios: string;
}