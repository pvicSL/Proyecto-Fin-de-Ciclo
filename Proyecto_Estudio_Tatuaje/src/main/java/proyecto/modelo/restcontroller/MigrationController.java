package proyecto.modelo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.repository.TrabajadorRepository;

@RestController
public class MigrationController {

    @Autowired
    private TrabajadorRepository trabajadorRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/migrar-contraseñas") // ¡SOLO PARA DESARROLLO!
    public ResponseEntity<?> migrarContrasenias() {
        List<Trabajador> todos = trabajadorRepository.findAll();
        
        for (Trabajador trabajador : todos) {
            String contraseniaPlana = trabajador.getContrasenia();
            
            // Solo migrar si no está ya encriptada (bcrypt siempre empieza con $2a$)
            if (!contraseniaPlana.startsWith("$2a$")) {
                String contraseniaEncriptada = passwordEncoder.encode(contraseniaPlana);
                trabajador.setContrasenia(contraseniaEncriptada);
                trabajadorRepository.save(trabajador);
                
                System.out.println("Migrada contraseña para: " + trabajador.getEmail());
            }
        }
        
        return ResponseEntity.ok("✅ Contraseñas migradas correctamente");
    }
}