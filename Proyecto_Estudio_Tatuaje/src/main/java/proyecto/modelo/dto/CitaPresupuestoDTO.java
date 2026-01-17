package proyecto.modelo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Presupuesto;

public class CitaPresupuestoDTO {

	// Datos de la cita
    private int idCita;
    private LocalDate fecha;
    private LocalTime hora;
    
    // Datos del servicio (como String para el frontend)
    private String tipo;
    
    // Datos del cliente (SIN idCliente, SIN dni)
    private String clienteNombre;
    private String clienteApellido1;
    private String clienteApellido2;
    

    public CitaPresupuestoDTO() {}

    public CitaPresupuestoDTO(int idCita, LocalDate fecha, LocalTime hora, String tipo, 
                              String clienteNombre, String clienteApellido1, String clienteApellido2) {
        this.idCita = idCita;
        this.fecha = fecha;
        this.hora = hora;
        this.tipo = tipo;
        this.clienteNombre = clienteNombre;
        this.clienteApellido1 = clienteApellido1;
        this.clienteApellido2 = clienteApellido2;
    }

	public int getIdCita() {
		return idCita;
	}

	public void setIdCita(int idCita) {
		this.idCita = idCita;
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

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
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
    
	/**
     * Constructor de conveniencia para usar en el Stream del Service.
     * Recibe la Cita (con su Cliente) y el Presupuesto.
     */
    public CitaPresupuestoDTO(Cita cita, Presupuesto presupuesto) {
        if (cita != null) {
            this.idCita = cita.getIdCita();
            this.fecha = cita.getFecha(); 
            this.hora = cita.getHora();  
            this.tipo = (cita.getTipo() != null) ? cita.getTipo().toString() : "N/A";
            
            if (cita.getCliente() != null) {
                this.clienteNombre = cita.getCliente().getNombre();
                this.clienteApellido1 = cita.getCliente().getApellido1();
                this.clienteApellido2 = cita.getCliente().getApellido2();
            }
        }
    }
}
