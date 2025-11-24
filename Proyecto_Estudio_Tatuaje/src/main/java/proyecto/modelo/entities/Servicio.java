package proyecto.modelo.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import proyecto.modelo.enums.Coloracion;
import proyecto.modelo.enums.Detalle;
import proyecto.modelo.enums.Estatus;
import proyecto.modelo.enums.Estilo;
import proyecto.modelo.enums.Tamanio;
import proyecto.modelo.enums.Tipo;
import proyecto.modelo.enums.Zona;

@Entity
@Table(name="USUARIOS")
public class Servicio implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5431666055805619336L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_servicio")
	private int idServicio;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tipo tipo;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Zona zona;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Tamanio tamanio;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Detalle detalle;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Coloracion coloracion;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Estilo estilo;
	
	@Temporal(TemporalType.DATE)
	private LocalDate fecha;
	
	@Temporal(TemporalType.TIME)
	private LocalTime hora;
	
	private String comentarios;
	private Boolean factura;
	
	@Enumerated(EnumType.STRING)
	private Estatus estatus;

	
	/*Relación con Cliente*/
	@ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;


	public Servicio() {
		super();
	}
	
	

	public Servicio(int idServicio, Tipo tipo, Zona zona, Tamanio tamanio, Detalle detalle, Coloracion coloracion,
			Estilo estilo, LocalDate fecha, LocalTime hora, String comentarios, Boolean factura, Estatus estatus,
			Cliente cliente) {
		super();
		this.idServicio = idServicio;
		this.tipo = tipo;
		this.zona = zona;
		this.tamanio = tamanio;
		this.detalle = detalle;
		this.coloracion = coloracion;
		this.estilo = estilo;
		this.fecha = fecha;
		this.hora = hora;
		this.comentarios = comentarios;
		this.factura = factura;
		this.estatus = estatus;
		this.cliente = cliente;
	}



	public int getIdServicio() {
		return idServicio;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public Zona getZona() {
		return zona;
	}

	public Tamanio getTamanio() {
		return tamanio;
	}

	public Detalle getDetalle() {
		return detalle;
	}

	public Estilo getEstilo() {
		return estilo;
	}

	public String getComentarios() {
		return comentarios;
	}

	public Cliente getCliente() {
		return cliente;
	}
	
	public Boolean getFactura() {
		return factura;
	}

	public void setFactura(Boolean factura) {
		this.factura = factura;
	}

	public Estatus getEstatus() {
		return estatus;
	}


	public void setEstatus(Estatus estatus) {
		this.estatus = estatus;
	}
	

	@Override
	public String toString() {
		return "Servicio [idServicio=" + idServicio + ", tipo=" + tipo + ", zona=" + zona + ", tamanio=" + tamanio
				+ ", detalle=" + detalle + ", estilo=" + estilo + ", comentarios=" + comentarios + ", cliente="
				+ cliente + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(idServicio);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Servicio))
			return false;
		Servicio other = (Servicio) obj;
		return idServicio == other.idServicio;
	}

	
}
