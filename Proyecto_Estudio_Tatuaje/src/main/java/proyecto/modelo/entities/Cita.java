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
@Table(name = "servicios")
public class Cita implements Serializable {
	private static final long serialVersionUID = -5431666055805619336L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_servicio") 
	private int idCita; 

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

	@Column(name = "duracion_minutos")
	private Integer duracionMinutos;

	@Column(name = "imagen_ref_1")
	private String imagenRef1;

	@Column(name = "imagen_ref_2")
	private String imagenRef2;

	@Column(name = "imagen_ref_3")
	private String imagenRef3;

	@Column(unique = true)
	private String referencia;

	@ManyToOne
	@JoinColumn(name = "id_cliente")
	private Cliente cliente;
	
	@ManyToOne
	@JoinColumn(name = "id_trabajador")
	private Trabajador trabajador;

	// CONSTRUCTORES
	public Cita() {
		super();
	}

	public Integer getDuracionMinutos() {
		return duracionMinutos;
	}

	public void setDuracionMinutos(Integer duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
	}

	public Cita(int idCita, Tipo tipo, Zona zona, Tamanio tamanio, Detalle detalle, Coloracion coloracion,
			Estilo estilo, LocalDate fecha, LocalTime hora, String comentarios, Boolean factura, Estatus estatus,
			Integer duracionMinutos, Cliente cliente) {
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
		this.factura = factura;
		this.estatus = estatus;
		this.duracionMinutos = duracionMinutos;
		this.cliente = cliente;
	}

	// GETTERS Y SETTERS
	public int getIdCita() {
		return idCita;
	}

	public void setIdCita(int idCita) {
		this.idCita = idCita;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Zona getZona() {
		return zona;
	}

	public void setZona(Zona zona) {
		this.zona = zona;
	}

	public Tamanio getTamanio() {
		return tamanio;
	}

	public void setTamanio(Tamanio tamanio) {
		this.tamanio = tamanio;
	}

	public Detalle getDetalle() {
		return detalle;
	}

	public void setDetalle(Detalle detalle) {
		this.detalle = detalle;
	}

	public Coloracion getColoracion() {
		return coloracion;
	}

	public void setColoracion(Coloracion coloracion) {
		this.coloracion = coloracion;
	}

	public Estilo getEstilo() {
		return estilo;
	}

	public void setEstilo(Estilo estilo) {
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

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
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

	// GETTERS Y SETTERS NUEVOS (SUBIDA IMAGENES)
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

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	
	public Trabajador getTrabajador() {
	    return trabajador;
	}

	public void setTrabajador(Trabajador trabajador) {
	    this.trabajador = trabajador;
	}

	@Override
	public String toString() {
		return "Cita [idCita=" + idCita + ", tipo=" + tipo + ", zona=" + zona + ", tamanio=" + tamanio + ", detalle="
				+ detalle + ", estilo=" + estilo + ", fecha=" + fecha + ", hora=" + hora + ", comentarios="
				+ comentarios + ", cliente=" + cliente + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(idCita);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Cita))
			return false;
		Cita other = (Cita) obj;
		return idCita == other.idCita;
	}
}
