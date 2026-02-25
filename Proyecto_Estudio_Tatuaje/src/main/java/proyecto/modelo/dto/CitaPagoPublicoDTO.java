package proyecto.modelo.dto;

import java.math.BigDecimal;

import proyecto.modelo.entities.Cita;

/**
 * DTO específico para la vista pública de pago.
 * Extiende de CitaDTO para heredar sus propiedades sin modificar la clase original,
 * previniendo conflictos en otros endpoints que usen CitaDTO.
 */

public class CitaPagoPublicoDTO extends CitaDTO  {
    // Añadimos exclusivamente las propiedades que necesita la pasarela de pago
    private BigDecimal precioTotal;
    private BigDecimal fianza;

    /**
     * Constructor que recibe la entidad Cita.
     * Utiliza super(cita) para ejecutar el constructor de la clase padre (CitaDTO)
     * y mapear automáticamente todos los datos básicos (fecha, hora, referencia, etc.).
     */
    public CitaPagoPublicoDTO(Cita cita) {
        super(cita); 
    }


    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public BigDecimal getFianza() {
        return fianza;
    }

    public void setFianza(BigDecimal fianza) {
        this.fianza = fianza;
    }
}


