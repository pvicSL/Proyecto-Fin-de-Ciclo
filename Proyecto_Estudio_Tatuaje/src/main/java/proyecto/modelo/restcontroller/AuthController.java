package proyecto.modelo.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import proyecto.modelo.entities.Trabajador;
import proyecto.security.AuthenticationService;
import proyecto.security.ErrorResponse;
import proyecto.security.JwtService;
import proyecto.security.LoginRequest;
import proyecto.security.LoginResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JwtService jwtService;

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
}
