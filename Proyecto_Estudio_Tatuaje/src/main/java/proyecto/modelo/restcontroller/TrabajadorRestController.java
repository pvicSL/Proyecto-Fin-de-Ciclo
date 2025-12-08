package proyecto.modelo.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.entities.Trabajador;
import proyecto.service.TrabajadorService;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class TrabajadorRestController {

	@Autowired
	private TrabajadorService trabajadorService;
	
	
	@GetMapping("/trabajadores")
	public List<Trabajador>leerTodos(){
		return trabajadorService.leerTodos();
	}
	
	@GetMapping("/trabajadores/{idTrabajador}")
	public ResponseEntity<?>buscarTrabajador(@PathVariable int idTrabajador) {
		if (trabajadorService.buscarUnTrabajador(idTrabajador) == null) {
			return new ResponseEntity<String>("No hay ningún trabajador con ese Id", HttpStatusCode.valueOf(404));
		} else {
			return new ResponseEntity<Trabajador>(trabajadorService.buscarUnTrabajador(idTrabajador), HttpStatusCode.valueOf(200));
		}
	}
	
	@PostMapping("/trabajador-alta")
	public ResponseEntity<?>altaTrabajador(@RequestBody Trabajador trabajador) {
		try {
			Trabajador confirmacionTrabajador = trabajadorService.altaTrabajador(trabajador);
			return new ResponseEntity<Trabajador>(confirmacionTrabajador, HttpStatusCode.valueOf(200));
		}catch (Exception e) {
			return new ResponseEntity<String>("Error al dar de alta al trabajador.", HttpStatusCode.valueOf(500));
		}
	}
	
	@GetMapping("/trabajador-dni/{documento}")
	public ResponseEntity<?>buscarTrabajadorPorDni(@PathVariable String documento) {
		Optional<Trabajador> trabajador = trabajadorService.buscarPorDocumento(documento);
		if (trabajador.isPresent()) {
			return ResponseEntity.ok(trabajador.get());		// Equivale a: new ResponseEntity<>(cliente.get(), HttpStatus.OK)
		} else {
			return new ResponseEntity<>("Cliente no encontrado con documento: " + documento, HttpStatusCode.valueOf(404));
			
		}
	}
	
	
	
}
