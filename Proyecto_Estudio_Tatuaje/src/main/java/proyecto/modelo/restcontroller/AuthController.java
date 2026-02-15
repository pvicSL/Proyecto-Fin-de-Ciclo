package proyecto.modelo.restcontroller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import proyecto.modelo.entities.PasswordResetToken;
import proyecto.modelo.entities.Trabajador;
import proyecto.security.AuthenticationService;
import proyecto.security.ErrorResponse;
import proyecto.security.JwtService;
import proyecto.security.LoginRequest;
import proyecto.security.LoginResponse;
import proyecto.service.EmailService;
import proyecto.service.PasswordResetTokenService;
import proyecto.service.TrabajadorService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private TrabajadorService trabajadorService;
    
    @Autowired
    private PasswordResetTokenService tokenService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1. Autenticar y obtener trabajador completo
            Trabajador trabajador = authenticationService.autenticarYObtenerUsuario(
                request.getEmail(), 
                request.getPassword()
            );

            // 2. Generar token
            String token = jwtService.generateToken(trabajador);

            // 3. Devolver respuesta completa
            return ResponseEntity.ok(new LoginResponse(
                token,
                "Login exitoso",
                trabajador.getIdTrabajador(), // Usar idTrabajador (int)
                trabajador.getEmail(),
                trabajador.getNombre(),
                trabajador.getRol().name() // "ADMIN" o "TRABAJADOR"
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                new ErrorResponse("Error", "Credenciales inválidas")
            );
        }
    }
    
    /**
     * POST /auth/forgot-password
     * Solicitar recuperación de contraseña por email
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> solicitarRecuperacion(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "El email es requerido"));
            }
            
            // 1. Buscar trabajador por email
            Optional<Trabajador> trabajadorOpt = trabajadorService.findByEmail(email); // ✅
            if (trabajadorOpt.isEmpty()) {
                // Por seguridad, no revelamos si el email existe o no
                return ResponseEntity.ok()
                    .body(Map.of("mensaje", "Si el email existe, recibirás un enlace de recuperación"));
            }

            Trabajador trabajador = trabajadorOpt.get();
            
            // 2. Crear token de recuperación
            PasswordResetToken resetToken = tokenService.crearToken(trabajador);
            
            // 3. Enviar email con el token
            emailService.enviarEmailRecuperacion(email, resetToken.getToken());
            
            return ResponseEntity.ok()
                .body(Map.of("mensaje", "Si el email existe, recibirás un enlace de recuperación"));
            
        } catch (Exception e) {
            System.err.println("Error en forgot-password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }

    /**
     * POST /auth/reset-password
     * Restablecer contraseña usando token
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> restablecerContrasenia(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String nuevaContrasenia = request.get("nuevaContrasenia");
            
            // 1. Validar parámetros
            if (token == null || token.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token es requerido"));
            }
            
            if (nuevaContrasenia == null || nuevaContrasenia.length() < 6) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña debe tener al menos 6 caracteres"));
            }
            
            // 2. Validar token
            if (!tokenService.esTokenValidoYNoExpirado(token)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token inválido o expirado"));
            }
            
            // 3. Obtener el trabajador del token
            PasswordResetToken resetToken = tokenService.buscarPorToken(token);
            Trabajador trabajador = resetToken.getTrabajador();
            
            // 4. Actualizar contraseña
            trabajador.setContrasenia(passwordEncoder.encode(nuevaContrasenia));
            trabajadorService.actualizarTrabajador(trabajador);
            
            // 5. Marcar token como utilizado
            tokenService.marcarComoUtilizado(token);
            
            // 6. Enviar email de confirmación (opcional)
            emailService.enviarEmailConfirmacion(
                trabajador.getEmail(), 
                "Tu contraseña ha sido actualizada correctamente en TatuSys."
            );
            
            return ResponseEntity.ok()
                .body(Map.of("mensaje", "Contraseña actualizada correctamente"));
            
        } catch (Exception e) {
            System.err.println("Error en reset-password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error interno del servidor"));
        }
    }
}
