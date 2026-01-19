package proyecto.modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class CitaCompletaDTO {
	// Datos Cita
    private int idCita;
    private String tipo, zona, tamanio, detalle, coloracion, estilo;
    private LocalDate fecha;
    private LocalTime hora;
    private String comentarios;
    private String imagenRef1, imagenRef2, imagenRef3;

    // Datos Cliente
    private String clienteNombre, clienteApellido1, clienteApellido2;
    private String clienteEmail, clienteTelefono, clienteDocumentoIdentificacion;

    // Datos Presupuesto
    private BigDecimal precioSinIva, iva, precioFinal;
    private LocalDateTime presupuestoFecha;
    private String estadoPresupuesto;
    private boolean vigente;
    private String presupuestoComentarios;
    
    // NUEVOS: Desglose de precios individuales
    private BigDecimal precioTipo;
    private BigDecimal precioZona;
    private BigDecimal precioTamanio;
    private BigDecimal precioDetalle;
    private BigDecimal precioColoracion;
    private BigDecimal precioEstilo;
    
    
	public CitaCompletaDTO() {
		super();
		
	}


	public CitaCompletaDTO(int idCita, String tipo, String zona, String tamanio, String detalle, String coloracion,
			String estilo, LocalDate fecha, LocalTime hora, String comentarios, String imagenRef1, String imagenRef2,
			String imagenRef3, String clienteNombre, String clienteApellido1, String clienteApellido2,
			String clienteEmail, String clienteTelefono, String clienteDocumentoIdentificacion, BigDecimal precioSinIva,
			BigDecimal iva, BigDecimal precioFinal, LocalDateTime presupuestoFecha, String estadoPresupuesto,
			boolean vigente, String presupuestoComentarios, BigDecimal precioTipo, BigDecimal precioZona,
			BigDecimal precioTamanio, BigDecimal precioDetalle, BigDecimal precioColoracion, BigDecimal precioEstilo) {
		super();
		this.idCita = idCita;
		this.tipo = tipo;
		this.zona = zona;
		this.tamanio = tamanio;
		this.detalle = detalle;
		this.coloracion = coloracion;
		this.estilo = estilo;
		this.fecha = fecha;
		this.hora = hora;
		this.comentarios = comentarios;
		this.imagenRef1 = imagenRef1;
		this.imagenRef2 = imagenRef2;
		this.imagenRef3 = imagenRef3;
		this.clienteNombre = clienteNombre;
		this.clienteApellido1 = clienteApellido1;
		this.clienteApellido2 = clienteApellido2;
		this.clienteEmail = clienteEmail;
		this.clienteTelefono = clienteTelefono;
		this.clienteDocumentoIdentificacion = clienteDocumentoIdentificacion;
		this.precioSinIva = precioSinIva;
		this.iva = iva;
		this.precioFinal = precioFinal;
		this.presupuestoFecha = presupuestoFecha;
		this.estadoPresupuesto = estadoPresupuesto;
		this.vigente = vigente;
		this.presupuestoComentarios = presupuestoComentarios;
		this.precioTipo = precioTipo;
		this.precioZona = precioZona;
		this.precioTamanio = precioTamanio;
		this.precioDetalle = precioDetalle;
		this.precioColoracion = precioColoracion;
		this.precioEstilo = precioEstilo;
	}
	
	// Constructor SIN precios individuales (para findCitaCompletaById)
	public CitaCompletaDTO(int idCita, String tipo, String zona, String tamanio, String detalle, String coloracion,
	        String estilo, LocalDate fecha, LocalTime hora, String comentarios, String imagenRef1, String imagenRef2,
	        String imagenRef3, String clienteNombre, String clienteApellido1, String clienteApellido2,
	        String clienteEmail, String clienteTelefono, String clienteDocumentoIdentificacion, BigDecimal precioSinIva,
	        BigDecimal iva, BigDecimal precioFinal, LocalDateTime presupuestoFecha, String estadoPresupuesto,
	        boolean vigente, String presupuestoComentarios) {
	    super();
	    this.idCita = idCita;
	    this.tipo = tipo;
	    this.zona = zona;
	    this.tamanio = tamanio;
	    this.detalle = detalle;
	    this.coloracion = coloracion;
	    this.estilo = estilo;
	    this.fecha = fecha;
	    this.hora = hora;
	    this.comentarios = comentarios;
	    this.imagenRef1 = imagenRef1;
	    this.imagenRef2 = imagenRef2;
	    this.imagenRef3 = imagenRef3;
	    this.clienteNombre = clienteNombre;
	    this.clienteApellido1 = clienteApellido1;
	    this.clienteApellido2 = clienteApellido2;
	    this.clienteEmail = clienteEmail;
	    this.clienteTelefono = clienteTelefono;
	    this.clienteDocumentoIdentificacion = clienteDocumentoIdentificacion;
	    this.precioSinIva = precioSinIva;
	    this.iva = iva;
	    this.precioFinal = precioFinal;
	    this.presupuestoFecha = presupuestoFecha;
	    this.estadoPresupuesto = estadoPresupuesto;
	    this.vigente = vigente;
	    this.presupuestoComentarios = presupuestoComentarios;
	    
	    // Los precios individuales quedan en null para este constructor
	    this.precioTipo = null;
	    this.precioZona = null;
	    this.precioTamanio = null;
	    this.precioDetalle = null;
	    this.precioColoracion = null;
	    this.precioEstilo = null;
	}


	public int getIdCita() {
		return idCita;
	}



	public void setIdCita(int idCita) {
		this.idCita = idCita;
	}


	public String getTipo() {
		return tipo;
	}


	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	public String getZona() {
		return zona;
	}


	public void setZona(String zona) {
		this.zona = zona;
	}

	public String getTamanio() {
		return tamanio;
	}

	public void setTamanio(String tamanio) {
		this.tamanio = tamanio;
	}


	public String getDetalle() {
		return detalle;
	}


	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public String getColoracion() {
		return coloracion;
	}

	public void setColoracion(String coloracion) {
		this.coloracion = coloracion;
	}


	public String getEstilo() {
		return estilo;
	}


	public void setEstilo(String estilo) {
		this.estilo = estilo;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHora() {
		return hora;
	}


	public void setHora(LocalTime hora) {
		this.hora = hora;
	}

	public String getComentarios() {
		return comentarios;
	}



	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}


	public String getImagenRef1() {
		return imagenRef1;
	}


	public void setImagenRef1(String imagenRef1) {
		this.imagenRef1 = imagenRef1;
	}


	public String getImagenRef2() {
		return imagenRef2;
	}


	public void setImagenRef2(String imagenRef2) {
		this.imagenRef2 = imagenRef2;
	}



	public String getImagenRef3() {
		return imagenRef3;
	}


	public void setImagenRef3(String imagenRef3) {
		this.imagenRef3 = imagenRef3;
	}

	public String getClienteNombre() {
		return clienteNombre;
	}


	public void setClienteNombre(String clienteNombre) {
		this.clienteNombre = clienteNombre;
	}



	public String getClienteApellido1() {
		return clienteApellido1;
	}


	public void setClienteApellido1(String clienteApellido1) {
		this.clienteApellido1 = clienteApellido1;
	}


	public String getClienteApellido2() {
		return clienteApellido2;
	}


	public void setClienteApellido2(String clienteApellido2) {
		this.clienteApellido2 = clienteApellido2;
	}

	public String getClienteEmail() {
		return clienteEmail;
	}


	public void setClienteEmail(String clienteEmail) {
		this.clienteEmail = clienteEmail;
	}


	public String getClienteTelefono() {
		return clienteTelefono;
	}


	public void setClienteTelefono(String clienteTelefono) {
		this.clienteTelefono = clienteTelefono;
	}


	public String getClienteDocumentoIdentificacion() {
		return clienteDocumentoIdentificacion;
	}


	public void setClienteDocumentoIdentificacion(String clienteDocumentoIdentificacion) {
		this.clienteDocumentoIdentificacion = clienteDocumentoIdentificacion;
	}


	public BigDecimal getPrecioSinIva() {
		return precioSinIva;
	}


	public void setPrecioSinIva(BigDecimal precioSinIva) {
		this.precioSinIva = precioSinIva;
	}


	public BigDecimal getIva() {
		return iva;
	}


	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}

	public BigDecimal getPrecioFinal() {
		return precioFinal;
	}



	public void setPrecioFinal(BigDecimal precioFinal) {
		this.precioFinal = precioFinal;
	}

	public LocalDateTime getPresupuestoFecha() {
		return presupuestoFecha;
	}


	public void setPresupuestoFecha(LocalDateTime presupuestoFecha) {
		this.presupuestoFecha = presupuestoFecha;
	}



	public String getEstadoPresupuesto() {
		return estadoPresupuesto;
	}

	public void setEstadoPresupuesto(String estadoPresupuesto) {
		this.estadoPresupuesto = estadoPresupuesto;
	}

	public boolean isVigente() {
		return vigente;
	}

	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}


	public String getPresupuestoComentarios() {
		return presupuestoComentarios;
	}

	public void setPresupuestoComentarios(String presupuestoComentarios) {
		this.presupuestoComentarios = presupuestoComentarios;
	}

	public BigDecimal getPrecioTipo() {
		return precioTipo;
	}

	public void setPrecioTipo(BigDecimal precioTipo) {
		this.precioTipo = precioTipo;
	}


	public BigDecimal getPrecioZona() {
		return precioZona;
	}


	public void setPrecioZona(BigDecimal precioZona) {
		this.precioZona = precioZona;
	}


	public BigDecimal getPrecioTamanio() {
		return precioTamanio;
	}

	public void setPrecioTamanio(BigDecimal precioTamanio) {
		this.precioTamanio = precioTamanio;
	}


	public BigDecimal getPrecioDetalle() {
		return precioDetalle;
	}


	public void setPrecioDetalle(BigDecimal precioDetalle) {
		this.precioDetalle = precioDetalle;
	}

	public BigDecimal getPrecioColoracion() {
		return precioColoracion;
	}

	public void setPrecioColoracion(BigDecimal precioColoracion) {
		this.precioColoracion = precioColoracion;
	}

	public BigDecimal getPrecioEstilo() {
		return precioEstilo;
	}

	public void setPrecioEstilo(BigDecimal precioEstilo) {
		this.precioEstilo = precioEstilo;
	}


	@Override
	public int hashCode() {
		return Objects.hash(clienteDocumentoIdentificacion);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CitaCompletaDTO))
			return false;
		CitaCompletaDTO other = (CitaCompletaDTO) obj;
		return Objects.equals(clienteDocumentoIdentificacion, other.clienteDocumentoIdentificacion);
	}


	/**
	 * Este método no es un atributo real, pero Jackson (el conversor de JSON)
	 * lo detectará y enviará al frontend una propiedad llamada "apellidosCompletos"
	 */
	public String getApellidosCompletos() {
	    return this.clienteApellido1 + (this.clienteApellido2 != null && !this.clienteApellido2.isEmpty() ? " " + this.clienteApellido2 : "");
	}
    
   
}