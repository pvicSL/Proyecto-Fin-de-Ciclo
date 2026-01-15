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
  comentariosServicio: string;
  fecha: string;
  hora: string;

  imagenRef1: string;
  imagenRef2: string;
  imagenRef3: string;

  // Datos del Presupuesto
  precioBase: number;
  iva: number;
  precioFinal: number;
  estadoPresupuesto: string;
  fechaPresupuesto: string;
  comentarios: string;
  vigente: boolean;
  
  // NUEVOS: Desglose de precios individuales
  precioTipo: number;
  precioZona: number;
  precioTamanio: number;
  precioDetalle: number;
  precioColoracion: number;
  precioEstilo: number;
}

