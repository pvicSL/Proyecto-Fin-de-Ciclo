export interface AppointmentDTO {
    // Datos de identificación
    idCita: number;

    // Datos temporales (Strings porque vienen así de JSON/Java)
    fecha: string; // 'yyyy-MM-dd'
    hora: string;  // 'HH:mm:ss'

    // Datos del servicio
    comentarios?: string;
    factura?: boolean;
    tipo: string;
    zona: string;
    tamanio: string; // CLAVE para calcular duración
    detalle: string;
    coloracion: string;
    estilo: string;
    estatus: string; // 'PENDIENTE', 'CONFIRMADO', etc.

    // Datos del cliente
    clienteNombre: string;
    clienteApellido1: string;
    clienteApellido2?: string;
    clienteEmail: string;
    clienteTelefono: string;
}