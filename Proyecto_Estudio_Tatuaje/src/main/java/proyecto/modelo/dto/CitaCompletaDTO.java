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
    private BigDecimal precioBase, iva, precioFinal;
    private LocalDateTime presupuestoFecha;
    private String estadoPresupuesto;
    private boolean vigente;
    private String presupuestoComentarios;
    
	public CitaCompletaDTO() {
		super();
		
	}

	public CitaCompletaDTO(int idCita, String tipo, String zona, String tamanio, String detalle, String coloracion,
			String estilo, LocalDate fecha, LocalTime hora, String comentarios, String imagenRef1, String imagenRef2,
			String imagenRef3, String clienteNombre, String clienteApellido1, String clienteApellido2,
			String clienteEmail, String clienteTelefono, String clienteDocumentoIdentificacion, BigDecimal precioBase,
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
		this.precioBase = precioBase;
		this.iva = iva;
		this.precioFinal = precioFinal;
		this.presupuestoFecha = presupuestoFecha;
		this.estadoPresupuesto = estadoPresupuesto;
		this.vigente = vigente;
		this.presupuestoComentarios = presupuestoComentarios;
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

	public BigDecimal getPrecioBase() {
		return precioBase;
	}

	public void setPrecioBase(BigDecimal precioBase) {
		this.precioBase = precioBase;
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

	@Override
	public int hashCode() {
		return Objects.hash(clienteApellido1, clienteApellido2, clienteDocumentoIdentificacion, clienteEmail,
				clienteNombre, clienteTelefono, coloracion, comentarios, detalle, estadoPresupuesto, estilo, fecha,
				hora, idCita, imagenRef1, imagenRef2, imagenRef3, iva, precioBase, precioFinal, presupuestoComentarios,
				presupuestoFecha, tamanio, tipo, vigente, zona);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof CitaCompletaDTO))
			return false;
		CitaCompletaDTO other = (CitaCompletaDTO) obj;
		return Objects.equals(clienteApellido1, other.clienteApellido1)
				&& Objects.equals(clienteApellido2, other.clienteApellido2)
				&& Objects.equals(clienteDocumentoIdentificacion, other.clienteDocumentoIdentificacion)
				&& Objects.equals(clienteEmail, other.clienteEmail)
				&& Objects.equals(clienteNombre, other.clienteNombre)
				&& Objects.equals(clienteTelefono, other.clienteTelefono)
				&& Objects.equals(coloracion, other.coloracion) && Objects.equals(comentarios, other.comentarios)
				&& Objects.equals(detalle, other.detalle) && Objects.equals(estadoPresupuesto, other.estadoPresupuesto)
				&& Objects.equals(estilo, other.estilo) && Objects.equals(fecha, other.fecha)
				&& Objects.equals(hora, other.hora) && idCita == other.idCita
				&& Objects.equals(imagenRef1, other.imagenRef1) && Objects.equals(imagenRef2, other.imagenRef2)
				&& Objects.equals(imagenRef3, other.imagenRef3) && Objects.equals(iva, other.iva)
				&& Objects.equals(precioBase, other.precioBase) && Objects.equals(precioFinal, other.precioFinal)
				&& Objects.equals(presupuestoComentarios, other.presupuestoComentarios)
				&& Objects.equals(presupuestoFecha, other.presupuestoFecha) && Objects.equals(tamanio, other.tamanio)
				&& Objects.equals(tipo, other.tipo) && vigente == other.vigente && Objects.equals(zona, other.zona);
	}
	
	/**
	 * Este método no es un atributo real, pero Jackson (el conversor de JSON)
	 * lo detectará y enviará al frontend una propiedad llamada "apellidosCompletos"
	 */
	public String getApellidosCompletos() {
	    return this.clienteApellido1 + (this.clienteApellido2 != null && !this.clienteApellido2.isEmpty() ? " " + this.clienteApellido2 : "");
	}
    
   
}