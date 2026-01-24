package proyecto.security;

public class LoginResponse {
    private String token;
    private String message;
    private int id;  // int porque tu campo es idTrabajador (int)
    private String email;
    private String nombre;
    private String rol; // "ADMIN" o "TRABAJADOR"

    public LoginResponse(String token, String message, int id, String email, String nombre, String rol) {
        this.token = token;
        this.message = message;
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
