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
    
    // Datos del servicio (como String para el frontend)
    private String tipo;
    private String zona;
    private String tamanio;
    private String detalle;
    private String coloracion;
    private String estilo;
    private String estatus;
    
    // Datos del cliente (SIN idCliente, SIN dni)
    private String clienteNombre;
    private String clienteApellido1;
    private String clienteApellido2;
    private String clienteEmail;
    private String clienteTelefono;

    // CONSTRUCTORES
    public CitaDTO() {}

    public CitaDTO(Cita cita) {
        this.idCita = cita.getIdCita();
        this.fecha = cita.getFecha();
        this.hora = cita.getHora();
        this.comentarios = cita.getComentarios();
        this.factura = cita.getFactura();
        
        // Enums → String
        this.tipo = cita.getTipo().toString();
        this.zona = cita.getZona().toString();
        this.tamanio = cita.getTamanio().toString();
        this.detalle = cita.getDetalle().toString();
        this.coloracion = cita.getColoracion().toString();
        this.estilo = cita.getEstilo().toString();
        this.estatus = cita.getEstatus().toString();
        
        // Datos del cliente (sin ID, sin DNI)
        this.clienteNombre = cita.getCliente().getNombre();
        this.clienteApellido1 = cita.getCliente().getApellido1();
        this.clienteApellido2 = cita.getCliente().getApellido2();
        this.clienteEmail = cita.getCliente().getEmail();
        this.clienteTelefono = cita.getCliente().getTelefono();
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
}
