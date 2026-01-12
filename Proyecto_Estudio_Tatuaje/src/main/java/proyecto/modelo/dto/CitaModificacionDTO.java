package proyecto.modelo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class CitaModificacionDTO {
    private String referencia;
    private String email;
    private LocalDate fecha; // Formato esperado "yyyy-MM-dd"
    private LocalTime hora;  // Formato esperado "HH:mm" OR "HH:mm:ss"

    // Getters y Setters
    public String getReferencia() { return referencia; }
    public void setReferencia(String referencia) { this.referencia = referencia; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    
    public LocalTime getHora() { return hora; }
    public void setHora(LocalTime hora) { this.hora = hora; }
}
