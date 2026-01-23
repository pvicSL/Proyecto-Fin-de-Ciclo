package proyecto.modelo.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyecto.security.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationService authenticationService;
    
    /**
     * Endpoint de login
     * POST /api/auth/login
     * Body: { "email": "admin@empresa.com", "password": "123456" }
     * Response: { "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1..." }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        
        try {
            // Autenticar y generar token
            String token = authenticationService.login(request.getEmail(), request.getPassword());
            
            // Respuesta exitosa
            return ResponseEntity.ok(new LoginResponse(token, "Login exitoso"));
            
        } catch (RuntimeException e) {
            // Credenciales incorrectas
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("Error", e.getMessage()));
        }
    } /*Para testear los logins con endpoints protegidos: en headers, key: Authorization, value: Bearer + tokken*/
    
    // Clases para estructurar las peticiones y respuestas JSON
    public static class LoginRequest {
        private String email;
        private String password;
        
        // Constructors, getters y setters
        public LoginRequest() {}
        
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    public static class LoginResponse {
        private String token;
        private String message;
        
        public LoginResponse(String token, String message) {
            this.token = token;
            this.message = message;
        }
        
        // Getters y setters
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
    
    public static class ErrorResponse {
        private String error;
        private String message;
        
        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }
        
        // Getters y setters
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}