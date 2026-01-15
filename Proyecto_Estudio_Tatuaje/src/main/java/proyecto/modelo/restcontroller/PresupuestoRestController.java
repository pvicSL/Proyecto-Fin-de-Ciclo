package proyecto.modelo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.entities.Presupuesto;
import proyecto.service.PresupuestoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PresupuestoRestController {

	
	@Autowired
	private PresupuestoService presupuestoService;
	
	@GetMapping("/presupuestos")
	public List<Presupuesto>leerTodos() {
		return presupuestoService.leerTodos();
	}
	
	@PostMapping("/presupuesto_nuevo")
	public ResponseEntity<Presupuesto> crearPresupuesto(@RequestBody int idServicio) {
        try {
            Presupuesto presupuesto = presupuestoService.calcularPresupuestoPorId(idServicio);
            return ResponseEntity.ok(presupuesto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
	
}
