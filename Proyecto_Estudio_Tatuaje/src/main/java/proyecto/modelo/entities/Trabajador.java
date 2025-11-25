package proyecto.modelo.entities;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import proyecto.modelo.enums.Funciones;
import proyecto.modelo.enums.Rol;


@Entity
@Table(name="trabajadores")
public class Trabajador implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 857462253530758914L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_trabajador")
	private int idTrabajador;
	@Column(nullable = false)
	private String contrasenia;
	@Column(nullable = false)
	private String nombre;
	@Column(nullable = false)
	private String apellido1;
	private String apellido2;
	@Column(nullable = false)
	private String email;
	private String telefono;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Rol rol;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Funciones funciones;

	public Trabajador() {
		super();
	}

	public Trabajador(String contrasenia, String nombre, String apellido1, String apellido2,
			String email, String telefono, Rol rol, Funciones funciones) {
		super();
		this.contrasenia = contrasenia;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.email = email;
		this.telefono = telefono;
		this.rol = rol;
		this.funciones = funciones;
	}

	public int getIdTrabajador() {
		return idTrabajador;
	}

	public void setIdTrabajador(int idTrabajador) {
		this.idTrabajador = idTrabajador;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Rol getRol() {
		return rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Funciones getFunciones() {
		return funciones;
	}

	public void setFunciones(Funciones funciones) {
		this.funciones = funciones;
	}

	@Override
	public String toString() {
		return "Trabajador [idTrabajador=" + idTrabajador + ", contrasenia=" + contrasenia + ", nombre=" + nombre
				+ ", apellido1=" + apellido1 + ", apellido2=" + apellido2 + ", email=" + email + ", telefono="
				+ telefono + ", rol=" + rol + ", funciones=" + funciones + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(idTrabajador);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Trabajador))
			return false;
		Trabajador other = (Trabajador) obj;
		return idTrabajador == other.idTrabajador;
	}

}
