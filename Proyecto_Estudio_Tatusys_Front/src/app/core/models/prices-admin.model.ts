export interface PricesAdminDTO{
    precioBase: number;
    precioTipo: number;
    precioZona: number;
    precioTamanio: number;
    precioDetalle: number;
    precioColoracion: number;
    precioEstilo: number;
    subtotal: number;
    iva: number;
    total: number;
}

export enum CategoriaEnum {
    BASE = 'BASE',
    TIPO = 'TIPO',
    ZONA = 'ZONA',
    TAMANIO = 'TAMANIO',
    DETALLE = 'DETALLE',
    COLORACION = 'COLORACION',
    ESTILO = 'ESTILO'
}

export interface Precio {
    idPrecio?: number;
    categoria: CategoriaEnum;
    valor: string;
    precioAdicional: number;
    activo: boolean;
}

