package proyecto.modelo.enums;

public enum EstadoFactura {
	NO_REQUIERE,    // cuando factura = false
    PENDIENTE,      // cuando factura = true (recién generado)
    ENVIADA         // cuando ya se envió la factura
}
