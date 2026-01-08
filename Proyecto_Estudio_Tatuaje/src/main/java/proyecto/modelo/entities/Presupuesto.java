package proyecto.modelo.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import proyecto.modelo.enums.Estado;

@Entity
@Table(name="presupuestos")
public class Presupuesto implements Serializable{

	
	
	
	private static final long serialVersionUID = 4214487646398039336L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_presupuesto")
	private int idPresupuesto;
	@Column(name="id_servicio")
	private int idServicio;
	@Column(name="precio_base")
	private BigDecimal precioBase;
	@Column(name = "precio_extra")
	private BigDecimal precioExtra;
	private BigDecimal iva;
	@Column(name="precio_final")
	private BigDecimal precioFinal;
	private LocalDateTime fecha;
	private boolean vigente;
	@Enumerated(EnumType.STRING)
	private Estado estado;
	private String comentarios;
	
	
	
	public Presupuesto() {
		super();
	}



	public Presupuesto(int idServicio, BigDecimal precioBase, BigDecimal precioExtra, 
            BigDecimal iva, BigDecimal precioFinal, LocalDateTime fecha, boolean vigente, 
            Estado estado, String comentarios) {
		super();
		this.idServicio = idServicio;
		this.precioBase = precioBase;
		this.precioExtra = precioExtra;
		this.iva = iva;
		this.precioFinal = precioFinal;
		this.fecha = fecha;
		this.vigente = vigente;
		this.estado = estado;
		this.comentarios = comentarios;
		}



	public int getIdPresupuesto() {
		return idPresupuesto;
	}



	public void setIdPresupuesto(int idPresupuesto) {
		this.idPresupuesto = idPresupuesto;
	}



	public int getIdServicio() {
		return idServicio;
	}



	public void setIdServicio(int idServicio) {
		this.idServicio = idServicio;
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



	public LocalDateTime getFecha() {
		return fecha;
	}



	public void setFecha(LocalDateTime fecha) {
		this.fecha = fecha;
	}



	public boolean isVigente() {
		return vigente;
	}



	public void setVigente(boolean vigente) {
		this.vigente = vigente;
	}



	public Estado getEstado() {
		return estado;
	}



	public void setEstado(Estado estado) {
		this.estado = estado;
	}



	public String getComentarios() {
		return comentarios;
	}



	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}
	
	



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	@Override
	public int hashCode() {
		return Objects.hash(idPresupuesto);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Presupuesto))
			return false;
		Presupuesto other = (Presupuesto) obj;
		return idPresupuesto == other.idPresupuesto;
	}



	@Override
	public String toString() {
		return "Presupuesto [idPresupuesto=" + idPresupuesto + ", idServicio=" + idServicio + ", precioBase="
				+ precioBase + ", iva=" + iva + ", precioFinal=" + precioFinal + ", fecha=" + fecha + ", vigente="
				+ vigente + ", estado=" + estado + ", comentarios=" + comentarios + "]";
	}
	
	
	

	
	
	
}
