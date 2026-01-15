package proyecto.modelo.dto;

import java.math.BigDecimal;

public class SimulacionDTO {

	private BigDecimal precioBase;
    private BigDecimal precioTipo;
    private BigDecimal precioZona;
    private BigDecimal precioTamanio;
    private BigDecimal precioDetalle;
    private BigDecimal precioColoracion;
    private BigDecimal precioEstilo;
    
    
    public SimulacionDTO() {
		super();
	}
	private int duracionMinutos;


	public SimulacionDTO(BigDecimal precioBase, BigDecimal precioTipo, BigDecimal precioZona, BigDecimal precioTamanio,
			BigDecimal precioDetalle, BigDecimal precioColoracion, BigDecimal precioEstilo, int duracionMinutos) {
		super();
		this.precioBase = precioBase;
		this.precioTipo = precioTipo;
		this.precioZona = precioZona;
		this.precioTamanio = precioTamanio;
		this.precioDetalle = precioDetalle;
		this.precioColoracion = precioColoracion;
		this.precioEstilo = precioEstilo;
		this.duracionMinutos = duracionMinutos;
	}


	public BigDecimal getPrecioBase() {
		return precioBase;
	}


	public void setPrecioBase(BigDecimal precioBase) {
		this.precioBase = precioBase;
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


	public int getDuracionMinutos() {
		return duracionMinutos;
	}


	public void setDuracionMinutos(int duracionMinutos) {
		this.duracionMinutos = duracionMinutos;
	}
	
	
}
