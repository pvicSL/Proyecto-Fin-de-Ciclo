package proyecto.modelo.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class PreciosIndividualesDTO {
    private BigDecimal precioBase;
    private BigDecimal precioTipo;
    private BigDecimal precioZona;
    private BigDecimal precioTamanio;
    private BigDecimal precioDetalle;
    private BigDecimal precioColoracion;
    private BigDecimal precioEstilo;
    private BigDecimal subtotal;
    private BigDecimal iva;
    private BigDecimal total;
    
	public PreciosIndividualesDTO() {
		super();
	}

	public PreciosIndividualesDTO(BigDecimal precioBase, BigDecimal precioTipo, BigDecimal precioZona,
			BigDecimal precioTamanio, BigDecimal precioDetalle, BigDecimal precioColoracion, BigDecimal precioEstilo,
			BigDecimal subtotal, BigDecimal iva, BigDecimal total) {
		super();
		this.precioBase = precioBase;
		this.precioTipo = precioTipo;
		this.precioZona = precioZona;
		this.precioTamanio = precioTamanio;
		this.precioDetalle = precioDetalle;
		this.precioColoracion = precioColoracion;
		this.precioEstilo = precioEstilo;
		this.subtotal = subtotal;
		this.iva = iva;
		this.total = total;
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

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getIva() {
		return iva;
	}

	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	@Override
	public int hashCode() {
		return Objects.hash(iva, precioBase, precioColoracion, precioDetalle, precioEstilo, precioTamanio, precioTipo,
				precioZona, subtotal, total);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PreciosIndividualesDTO))
			return false;
		PreciosIndividualesDTO other = (PreciosIndividualesDTO) obj;
		return Objects.equals(iva, other.iva) && Objects.equals(precioBase, other.precioBase)
				&& Objects.equals(precioColoracion, other.precioColoracion)
				&& Objects.equals(precioDetalle, other.precioDetalle)
				&& Objects.equals(precioEstilo, other.precioEstilo)
				&& Objects.equals(precioTamanio, other.precioTamanio) && Objects.equals(precioTipo, other.precioTipo)
				&& Objects.equals(precioZona, other.precioZona) && Objects.equals(subtotal, other.subtotal)
				&& Objects.equals(total, other.total);
	}
    
    
}