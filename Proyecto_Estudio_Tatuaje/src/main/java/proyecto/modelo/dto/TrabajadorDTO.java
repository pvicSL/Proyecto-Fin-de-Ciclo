package proyecto.modelo.dto;

import java.util.Objects;

import proyecto.modelo.entities.Trabajador;

public class TrabajadorDTO {
    
    // Datos seguros del trabajador
    private int idTrabajador;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String email;
    private String telefono;
    private String dni;
    
    
    // SIN: dni, numeroCuenta, contrasenia (DATOS SENSIBLES)

    // CONSTRUCTORES
    public TrabajadorDTO(Trabajador trabajador) {
        this.idTrabajador = trabajador.getIdTrabajador();
        this.nombre = trabajador.getNombre();
        this.apellido1 = trabajador.getApellido1();
        this.apellido2 = trabajador.getApellido2();
        this.email = trabajador.getEmail();
        this.telefono = trabajador.getTelefono();
        this.dni = trabajador.getDni();
    }

    

    public TrabajadorDTO() {
		super();
	}


	public TrabajadorDTO(int idTrabajador, String nombre, String apellido1, String apellido2, String email,
			String telefono, String dni) {
		super();
		this.idTrabajador = idTrabajador;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.email = email;
		this.telefono = telefono;
		this.dni = dni;
	}



	// GETTERS Y SETTERS
    public int getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(int idTrabajador) {
        this.idTrabajador = idTrabajador;
    }
    
    public String getDni() {
		return dni;
	}


	public void setDni(String dni) {
		this.dni = dni;
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

  

    @Override
    public String toString() {
        return "TrabajadorDTO [idTrabajador=" + idTrabajador + ", nombre=" + nombre + ", apellido1=" + apellido1
                + ", apellido2=" + apellido2 + ", email=" + email + ", telefono=" + telefono + "]";
    }



	@Override
	public int hashCode() {
		return Objects.hash(dni);
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof TrabajadorDTO))
			return false;
		TrabajadorDTO other = (TrabajadorDTO) obj;
		return Objects.equals(dni, other.dni);
	}
    
    
}