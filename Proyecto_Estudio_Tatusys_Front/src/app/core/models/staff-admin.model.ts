import { AppointmentDTO } from "./appointment.model";

export interface StaffAdminDTO{ 
    idTrabajador: number;
    dni: string;
    numeroCuenta: string;
    contrasenia: string;
    nombre: string;
    apellido1: string;
    apellido2: string;
    email: string;
    telefono: string;

    rol: string;
    funciones: string;


}

