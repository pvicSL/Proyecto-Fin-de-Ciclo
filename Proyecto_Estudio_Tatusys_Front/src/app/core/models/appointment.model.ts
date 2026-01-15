export interface AppointmentDTO {
    // Datos de identificación
    idCita: number;
    referencia?: string;

    // Datos de fecha y hora (son Strings porque vienen así de JSON/Java)
    fecha: string; // 'yyyy-MM-dd'
    hora: string;  // 'HH:mm:ss'

    // Datos del servicio (es decir, de la cita que se solicita)
    comentarios?: string;
    factura?: boolean;
    tipo: string;
    zona: string;
    tamanio: string; // IMP, se usa para calcular duración
    detalle: string;
    coloracion: string;
    estilo: string;
    estatus: string; // 'PENDIENTE', 'CONFIRMADA', etc.
    //NUEVO - DURACIÓN DE LA CITA
    duracionEstimada?: number;

    // Datos del cliente
    clienteNombre: string;
    clienteApellido1: string;
    clienteApellido2?: string;
    clienteEmail: string;
    clienteTelefono: string;
}

