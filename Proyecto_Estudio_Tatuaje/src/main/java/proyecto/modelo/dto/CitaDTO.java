package proyecto.modelo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import proyecto.modelo.entities.Cita;

public class CitaDTO {
    
    // Datos de la cita
    private int idCita;
    private LocalDate fecha;
    private LocalTime hora;
    private String comentarios;
    private Boolean factura;
    private String referencia;
    
    // Datos del servicio (como String para el frontend)
    private String tipo;
    private String zona;
    private String tamanio;
    private String detalle;
    private String coloracion;
    private String estilo;
    private String estatus;
    private Integer duracionEstimada; 
    
    // Datos del cliente
    private String clienteNombre;
    private String clienteApellido1;
    private String clienteApellido2;
    private String clienteEmail;
    private String clienteTelefono;

    // CONSTRUCTORES
    
    public CitaDTO() {
    }

    

	public CitaDTO(int idCita, LocalDate fecha, LocalTime hora, String comentarios, Boolean factura, String referencia,
			String tipo, String zona, String tamanio, String detalle, String coloracion, String estilo, String estatus,
			Integer duracionEstimada, String clienteNombre, String clienteApellido1, String clienteApellido2,
			String clienteEmail, String clienteTelefono) {
		super();
		this.idCita = idCita;
		this.fecha = fecha;
		this.hora = hora;
		this.comentarios = comentarios;
		this.factura = factura;
		this.setReferencia(referencia);
		this.tipo = tipo;
		this.zona = zona;
		this.tamanio = tamanio;
		this.detalle = detalle;
		this.coloracion = coloracion;
		this.estilo = estilo;
		this.estatus = estatus;
		this.duracionEstimada = duracionEstimada;
		this.clienteNombre = clienteNombre;
		this.clienteApellido1 = clienteApellido1;
		this.clienteApellido2 = clienteApellido2;
		this.clienteEmail = clienteEmail;
		this.clienteTelefono = clienteTelefono;
	}

	public CitaDTO(Cita cita) {
	    this.idCita = cita.getIdCita();
	    this.fecha = cita.getFecha();
	    this.hora = cita.getHora();
	    this.comentarios = cita.getComentarios();
	    this.factura = cita.getFactura();
	    this.referencia = cita.getReferencia();
	    
	    // Convertir enums a String
	    this.tipo = cita.getTipo() != null ? cita.getTipo().name() : null;
	    this.zona = cita.getZona() != null ? cita.getZona().name() : null;
	    this.tamanio = cita.getTamanio() != null ? cita.getTamanio().name() : null;
	    this.detalle = cita.getDetalle() != null ? cita.getDetalle().name() : null;
	    this.coloracion = cita.getColoracion() != null ? cita.getColoracion().name() : null;
	    this.estilo = cita.getEstilo() != null ? cita.getEstilo().name() : null;
	    this.estatus = cita.getEstatus() != null ? cita.getEstatus().name() : null;
	    
	    // Mapear duracionMinutos a duracionEstimada
	    this.duracionEstimada = cita.getDuracionMinutos();
	    
	    // Mapear datos del cliente
	    if (cita.getCliente() != null) {
	        this.clienteNombre = cita.getCliente().getNombre();
	        this.clienteApellido1 = cita.getCliente().getApellido1();
	        this.clienteApellido2 = cita.getCliente().getApellido2();
	        this.clienteEmail = cita.getCliente().getEmail();
	        this.clienteTelefono = cita.getCliente().getTelefono();
	    }
	}




	// GETTERS Y SETTERS
    public int getIdCita() { return idCita; }
    public void setIdCita(int idCita) { this.idCita = idCita; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }

    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }

    public Boolean getFactura() { return factura; }
    public void setFactura(Boolean factura) { this.factura = factura; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getZona() { return zona; }
    public void setZona(String zona) { this.zona = zona; }

    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getColoracion() { return coloracion; }
    public void setColoracion(String coloracion) { this.coloracion = coloracion; }

    public String getEstilo() { return estilo; }
    public void setEstilo(String estilo) { this.estilo = estilo; }

    public String getEstatus() { return estatus; }
    public void setEstatus(String estatus) { this.estatus = estatus; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteApellido1() { return clienteApellido1; }
    public void setClienteApellido1(String clienteApellido1) { this.clienteApellido1 = clienteApellido1; }

    public String getClienteApellido2() { return clienteApellido2; }
    public void setClienteApellido2(String clienteApellido2) { this.clienteApellido2 = clienteApellido2; }

    public String getClienteEmail() { return clienteEmail; }
    public void setClienteEmail(String clienteEmail) { this.clienteEmail = clienteEmail; }

    public String getClienteTelefono() { return clienteTelefono; }
    public void setClienteTelefono(String clienteTelefono) { this.clienteTelefono = clienteTelefono; }

	public Integer getDuracionEstimada() {
		return duracionEstimada;
	}

	public void setDuracionEstimada(Integer duracionEstimada) {
		this.duracionEstimada = duracionEstimada;
	}



	public String getReferencia() {
		return referencia;
	}



	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
}
