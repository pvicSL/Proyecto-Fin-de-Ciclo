export interface AppointmentAdminDTO {

  //Datos Cliente
  clienteNombre: string;
  clienteApellido1: string;
  clienteApellido2: string;
  clienteEmail: string;
  clienteTelefono: string;
  clienteDocumentoIdentificacion: string;
  
  // Datos del Servicio
  idCita: number;
  tipo: string;
  zona: string;
  tamanio: string;
  detalle: string;
  coloracion: string;
  estilo: string;
  comentarios: string;
  fecha: string;
  hora: string;

  imagenRef1: string;
  imagenRef2: string;
  imagenRef3: string;

  // Datos del Presupuesto
  precioSinIva: number;
  iva: number;
  precioFinal: number;
  estadoPresupuesto: string;
  fechaPresupuesto: string;
  presupuestoComentarios: string;
  vigente: boolean;
  
  // NUEVOS: Desglose de precios individuales
  precioTipo: number;
  precioZona: number;
  precioTamanio: number;
  precioDetalle: number;
  precioColoracion: number;
  precioEstilo: number;
}

