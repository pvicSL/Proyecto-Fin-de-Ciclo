package proyecto.modelo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import proyecto.modelo.enums.Estado;

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

    // --- DATOS DEL SERVICIO Y PRECIOS DESGLOSADOS ---
    
    private String baseServicio;
    private BigDecimal precioBaseServicio;
    
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
    
    private Integer duracionMinutos;

    private String comentariosServicio;
    
    private String imageRef1;
    
    private String imageRef2;
    
    private String imageRef3;

    // --- DATOS DEL PRESUPUESTO ---
    private int idPresupuesto;
    private BigDecimal precioBase;
    private BigDecimal precioExtra;
    private BigDecimal iva;
    private BigDecimal precioFinal;
    private LocalDateTime fechaPresupuesto;
    @Enumerated(EnumType.STRING)
    private Estado estadoPresupuesto;
    private String comentarios;

    // --- CONSTRUCTORES ---

    public CitaAdminDTO() {
    }

    public CitaAdminDTO(int idServicio, String nombre, String apellido1, String apellido2, String telefono, 
            String email, String dni, String baseServicio, BigDecimal precioBaseServicio, String tipo, BigDecimal precioTipo, 
            String zona, BigDecimal precioZona, String tamanio, BigDecimal precioTamanio, 
            String detalle, BigDecimal precioDetalle, String coloracion, BigDecimal precioColoracion, 
            String estilo, BigDecimal precioEstilo, Integer duracionMinutos, String comentariosServicio, String imageRef1, 
            String imageRef2, String imageRef3, int idPresupuesto, BigDecimal precioBase, 
            BigDecimal iva, BigDecimal precioFinal, LocalDateTime fechaPresupuesto, 
            Estado estadoPresupuesto, String comentarios) {
		this.idServicio = idServicio;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.telefono = telefono;
		this.email = email;
		this.dni = dni;
		this.baseServicio = baseServicio;
		this.precioBaseServicio = precioBaseServicio;
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
		this.duracionMinutos = duracionMinutos;
		this.comentariosServicio = comentariosServicio;
		this.imageRef1 = imageRef1;
		this.imageRef2 = imageRef2;
		this.imageRef3 = imageRef3;
		this.idPresupuesto = idPresupuesto;
		this.precioBase = precioBase;
		this.iva = iva;
		this.precioFinal = precioFinal;
		this.fechaPresupuesto = fechaPresupuesto;
		this.estadoPresupuesto = estadoPresupuesto;
		this.comentarios = comentarios;
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

	

	public String getBaseServicio() {
		return baseServicio;
	}

	public void setBaseServicio(String baseServicio) {
		this.baseServicio = baseServicio;
	}

	public BigDecimal getPrecioBaseServicio() {
		return precioBaseServicio;
	}

	public void setPrecioBaseServicio(BigDecimal precioBaseServicio) {
		this.precioBaseServicio = precioBaseServicio;
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
	

	public Integer getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(Integer duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
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

	
	public BigDecimal getPrecioExtra() {
		return precioExtra;
	}

	public void setPrecioExtra(BigDecimal precioExtra) {
		this.precioExtra = precioExtra;
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

	public Estado getEstadoPresupuesto() {
		return estadoPresupuesto;
	}

	public void setEstadoPresupuesto(Estado estadoPresupuesto) {
		this.estadoPresupuesto = estadoPresupuesto;
	}

	public String getComentarios() {
		return comentarios;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
    
	public String getImageRef1() {
		return imageRef1;
	}

	public void setImageRef1(String imageRef1) {
		this.imageRef1 = imageRef1;
	}

	public String getImageRef2() {
		return imageRef2;
	}

	public void setImageRef2(String imageRef2) {
		this.imageRef2 = imageRef2;
	}

	public String getImageRef3() {
		return imageRef3;
	}

	public void setImageRef3(String imageRef3) {
		this.imageRef3 = imageRef3;
	}

	/**
	 * Este método no es un atributo real, pero Jackson (el conversor de JSON) 
	 * lo detectará y enviará al frontend una propiedad llamada "apellidosCompletos"
	 */
	public String getApellidosCompletos() {
	    return this.apellido1 + (this.apellido2 != null && !this.apellido2.isEmpty() ? " " + this.apellido2 : "");
	}
}
