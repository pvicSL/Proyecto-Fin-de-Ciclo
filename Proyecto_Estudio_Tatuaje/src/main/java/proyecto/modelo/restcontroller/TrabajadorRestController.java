package proyecto.modelo.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.TrabajadorDTO;
import proyecto.modelo.entities.Trabajador;
import proyecto.service.TrabajadorService;

@RestController
@RequestMapping("/api/admin")
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
	
	//Version para TrabajadorDTO
	@GetMapping("/trabajadores/todos")
	public List<TrabajadorDTO> leerTodosDTO(){
	    List<Trabajador> trabajadores = trabajadorService.leerTodos();
	    return trabajadores.stream()
	            .map(TrabajadorDTO::new)
	            .collect(Collectors.toList());
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
	
	// Ver citas asignadas a un trabajador específico
	@GetMapping("/trabajadores/{trabajadorId}/citas")
	public ResponseEntity<?> obtenerCitasDelTrabajador(@PathVariable int trabajadorId) {
	    List<CitaDTO> citas = trabajadorService.obtenerCitasDelTrabajador(trabajadorId);
	    
	    if (citas.isEmpty()) {
	        return ResponseEntity.ok(Map.of("mensaje", "El trabajador no tiene citas asignadas"));
	    } else {
	        Map<String, Object> respuesta = new HashMap<>();
	        respuesta.put("trabajadorId", trabajadorId);
	        respuesta.put("totalCitas", citas.size());
	        respuesta.put("citas", citas);
	        return ResponseEntity.ok(respuesta);
	    }
	}

	// Eliminar trabajador (con validación de citas)
	@DeleteMapping("/trabajadores/{trabajadorId}")
	public ResponseEntity<?> eliminarTrabajador(@PathVariable int trabajadorId) {
	    int resultado = trabajadorService.eliminarTrabajador(trabajadorId);
	    
	    switch (resultado) {
	        case 0:
	            return ResponseEntity.ok(Map.of("mensaje", "Trabajador eliminado correctamente"));
	        
	        case -1:
	            return new ResponseEntity<>("No se encontró ningún trabajador con ID: " + trabajadorId, 
	                                      HttpStatusCode.valueOf(404));
	        
	        default: // resultado > 0 (número de citas)
	            return new ResponseEntity<>(
	                Map.of("error", "No se puede eliminar el trabajador. Tiene " + resultado + 
	                       " citas asignadas. Desasígnalas primero."), 
	                HttpStatusCode.valueOf(400));
	    }
	}
	
	
	
}
