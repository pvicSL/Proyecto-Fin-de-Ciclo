package proyecto.modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CitaAdminDTO {

	// ID de la cita/servicio
    private int idServicio;

    // --- DATOS DEL CLIENTE ---
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String telefono;
    private String email;
    private String dni;
    private String direccionPostal;

    // --- DATOS DEL SERVICIO Y PRECIOS DESGLOSADOS ---
    private String tipo;
    private BigDecimal precioTipo;

    private String zona;
    private BigDecimal precioZona;

    private String tamanio;
    private BigDecimal precioTamanio;

    private String detalle;
    private BigDecimal precioDetalle;

    private String coloracion;
    private BigDecimal precioColoracion;

    private String estilo;
    private BigDecimal precioEstilo;

    private String comentariosServicio;

    // --- DATOS DEL PRESUPUESTO ---
    private int idPresupuesto;
    private BigDecimal precioBase;
    private BigDecimal iva;
    private BigDecimal precioFinal;
    private LocalDateTime fechaPresupuesto;
    private String estadoPresupuesto;
    private String comentariosPresupuesto;

    // --- CONSTRUCTORES ---

    public CitaAdminDTO() {
    }

    public CitaAdminDTO(int idServicio, String nombre, String apellido1, String apellido2, String telefono, 
            String email, String dni, String direccionPostal, String tipo, BigDecimal precioTipo, 
            String zona, BigDecimal precioZona, String tamanio, BigDecimal precioTamanio, 
            String detalle, BigDecimal precioDetalle, String coloracion, BigDecimal precioColoracion, 
            String estilo, BigDecimal precioEstilo, String comentariosServicio, int idPresupuesto, 
            BigDecimal precioBase, BigDecimal iva, BigDecimal precioFinal, 
            LocalDateTime fechaPresupuesto, String estadoPresupuesto, String comentariosPresupuesto) {
		this.idServicio = idServicio;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.telefono = telefono;
		this.email = email;
		this.dni = dni;
		this.direccionPostal = direccionPostal;
		this.tipo = tipo;
		this.precioTipo = precioTipo;
		this.zona = zona;
		this.precioZona = precioZona;
		this.tamanio = tamanio;
		this.precioTamanio = precioTamanio;
		this.detalle = detalle;
		this.precioDetalle = precioDetalle;
		this.coloracion = coloracion;
		this.precioColoracion = precioColoracion;
		this.estilo = estilo;
		this.precioEstilo = precioEstilo;
		this.comentariosServicio = comentariosServicio;
		this.idPresupuesto = idPresupuesto;
		this.precioBase = precioBase;
		this.iva = iva;
		this.precioFinal = precioFinal;
		this.fechaPresupuesto = fechaPresupuesto;
		this.estadoPresupuesto = estadoPresupuesto;
		this.comentariosPresupuesto = comentariosPresupuesto;
    }

	public int getIdServicio() {
		return idServicio;
	}

	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido1() {
		return apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getDireccionPostal() {
		return direccionPostal;
	}

	public void setDireccionPostal(String direccionPostal) {
		this.direccionPostal = direccionPostal;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public BigDecimal getPrecioTipo() {
		return precioTipo;
	}

	public void setPrecioTipo(BigDecimal precioTipo) {
		this.precioTipo = precioTipo;
	}

	public String getZona() {
		return zona;
	}

	public void setZona(String zona) {
		this.zona = zona;
	}

	public BigDecimal getPrecioZona() {
		return precioZona;
	}

	public void setPrecioZona(BigDecimal precioZona) {
		this.precioZona = precioZona;
	}

	public String getTamanio() {
		return tamanio;
	}

	public void setTamanio(String tamanio) {
		this.tamanio = tamanio;
	}

	public BigDecimal getPrecioTamanio() {
		return precioTamanio;
	}

	public void setPrecioTamanio(BigDecimal precioTamanio) {
		this.precioTamanio = precioTamanio;
	}

	public String getDetalle() {
		return detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public BigDecimal getPrecioDetalle() {
		return precioDetalle;
	}

	public void setPrecioDetalle(BigDecimal precioDetalle) {
		this.precioDetalle = precioDetalle;
	}

	public String getColoracion() {
		return coloracion;
	}

	public void setColoracion(String coloracion) {
		this.coloracion = coloracion;
	}

	public BigDecimal getPrecioColoracion() {
		return precioColoracion;
	}

	public void setPrecioColoracion(BigDecimal precioColoracion) {
		this.precioColoracion = precioColoracion;
	}

	public String getEstilo() {
		return estilo;
	}

	public void setEstilo(String estilo) {
		this.estilo = estilo;
	}

	public BigDecimal getPrecioEstilo() {
		return precioEstilo;
	}

	public void setPrecioEstilo(BigDecimal precioEstilo) {
		this.precioEstilo = precioEstilo;
	}

	public String getComentariosServicio() {
		return comentariosServicio;
	}

	public void setComentariosServicio(String comentariosServicio) {
		this.comentariosServicio = comentariosServicio;
	}

	public int getIdPresupuesto() {
		return idPresupuesto;
	}

	public void setIdPresupuesto(int idPresupuesto) {
		this.idPresupuesto = idPresupuesto;
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

	public LocalDateTime getFechaPresupuesto() {
		return fechaPresupuesto;
	}

	public void setFechaPresupuesto(LocalDateTime fechaPresupuesto) {
		this.fechaPresupuesto = fechaPresupuesto;
	}

	public String getEstadoPresupuesto() {
		return estadoPresupuesto;
	}

	public void setEstadoPresupuesto(String estadoPresupuesto) {
		this.estadoPresupuesto = estadoPresupuesto;
	}

	public String getComentariosPresupuesto() {
		return comentariosPresupuesto;
	}

	public void setComentariosPresupuesto(String comentariosPresupuesto) {
		this.comentariosPresupuesto = comentariosPresupuesto;
	}
    
	/**
	 * Este método no es un atributo real, pero Jackson (el conversor de JSON) 
	 * lo detectará y enviará al frontend una propiedad llamada "apellidosCompletos"
	 */
	public String getApellidosCompletos() {
	    return this.apellido1 + (this.apellido2 != null && !this.apellido2.isEmpty() ? " " + this.apellido2 : "");
	}
}
