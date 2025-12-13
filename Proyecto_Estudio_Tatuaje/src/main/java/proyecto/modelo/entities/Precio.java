package proyecto.modelo.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import proyecto.modelo.enums.CategoriaEnum;

@Entity
@Table(name="precios_adicionales")
public class Precio implements Serializable{
	
	/*Solo almacena datos, no hace cálculos*/
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int idPrecio;
	@Enumerated(EnumType.STRING)
	private CategoriaEnum categoria;
	private String valor;
	@Column(name="precio_adicional")
	private BigDecimal precioAdicional;
	private boolean activo;
	
	
	public Precio() {
		super();
	}



	public Precio(CategoriaEnum categoria, String valor, BigDecimal precioAdicional, boolean activo) {
		super();
		this.categoria = categoria;
		this.valor = valor;
		this.precioAdicional = precioAdicional;
		this.activo = activo;
	}





	public int getIdPrecio() {
		return idPrecio;
	}


	public void setIdPrecio(int idPrecio) {
		this.idPrecio = idPrecio;
	}


	public CategoriaEnum getCategoria() {
		return categoria;
	}


	public void setCategoria(CategoriaEnum categoria) {
		this.categoria = categoria;
	}


	public String getValor() {
		return valor;
	}


	public void setValor(String valor) {
		this.valor = valor;
	}


	public BigDecimal getPrecioAdicional() {
		return precioAdicional;
	}


	public void setPrecioAdicional(BigDecimal precioAdicional) {
		this.precioAdicional = precioAdicional;
	}


	public boolean isActivo() {
		return activo;
	}


	public void setActivo(boolean activo) {
		this.activo = activo;
	}


	@Override
	public String toString() {
		return "Precio [idPrecio=" + idPrecio + ", categoria=" + categoria + ", valor=" + valor + ", precioAdicional="
				+ precioAdicional + ", activo=" + activo + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(idPrecio);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Precio))
			return false;
		Precio other = (Precio) obj;
		return idPrecio == other.idPrecio;
	}
	
	

}
