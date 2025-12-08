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
    public String login(String email, String password) {
        
        // 1. Buscar trabajador por email
        Trabajador trabajador = trabajadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Email o contraseña incorrectos"));
        
        // 2. Verificar contraseña (comparar texto plano con hash almacenado)
        if (!password.equals(trabajador.getContrasenia())) {
            throw new RuntimeException("Email o contraseña incorrectos");
        }
        
        // 3. Si llegamos aquí, las credenciales son correctas → generar token
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
