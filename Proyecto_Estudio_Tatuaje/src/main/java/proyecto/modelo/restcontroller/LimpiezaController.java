package proyecto.modelo.restcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.service.LimpiezaService;

@RestController
@RequestMapping("/api/limpieza")
public class LimpiezaController {

	
	@Autowired
    private LimpiezaService limpiezaService;
    
    @PostMapping("/ejecutar")
    public ResponseEntity<String> ejecutarLimpiezaManual() {
        try {
            limpiezaService.ejecutarLimpiezaManual();
            return ResponseEntity.ok("Limpieza ejecutada correctamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al ejecutar la limpieza: " + e.getMessage());
        }
    }
    
    @GetMapping("/estado")
    public ResponseEntity<String> verificarEstado() {
        return ResponseEntity.ok("Servicio de limpieza disponible");
    }
}

