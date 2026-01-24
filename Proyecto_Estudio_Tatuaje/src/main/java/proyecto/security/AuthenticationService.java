package proyecto.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.repository.TrabajadorRepository;

@Service
public class AuthenticationService {
    
    @Autowired
    private TrabajadorRepository trabajadorRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Autenticar trabajador con email y contraseña
     * @param email Email del trabajador
     * @param password Contraseña en texto plano
     * @return JWT token si las credenciales son correctas
     * @throws RuntimeException si las credenciales son incorrectas
     */
    public Trabajador autenticarYObtenerUsuario(String email, String password) {
        
        // 1. Buscar trabajador por email
        Trabajador trabajador = trabajadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
     /* 2. Verificar contraseña - usar passwordEncoder
        if (!passwordEncoder.matches(password, trabajador.getContrasenia())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }*/ //USAR CUANDO SE MIGREN LAS CONTRASEÑAS ENCRIPTADAS
        
     // 2. Verificar contraseña (MANTENEMOS equals temporalmente)
        if (!password.equals(trabajador.getContrasenia())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        
     // 3. Devolver trabajador completo
        return trabajador;
    }
    
    /**
     * Método original (mantener para compatibilidad)
     */
    public String login(String email, String password) {
        
        Trabajador trabajador = trabajadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));

        // CORREGIR: Usar passwordEncoder en lugar de equals
        if (!passwordEncoder.matches(password, trabajador.getContrasenia())) {
            throw new RuntimeException("Email o contraseña incorrectas");
        }

        return jwtService.generateToken(trabajador);
    }
    
    /**
     * Encriptar contraseña para almacenar en BD
     * @param rawPassword Contraseña en texto plano
     * @return Contraseña encriptada
     */
    public String encryptPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    /**
     * Verificar si una contraseña coincide con el hash almacenado
     * @param rawPassword Contraseña en texto plano
     * @param encodedPassword Hash almacenado en BD
     * @return true si coinciden
     */
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
