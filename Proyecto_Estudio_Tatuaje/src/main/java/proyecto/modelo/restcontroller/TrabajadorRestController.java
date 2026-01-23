package proyecto.modelo.restcontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.TrabajadorDTO;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.enums.Rol;
import proyecto.service.TrabajadorService;

@RestController
@RequestMapping("/api/admin")
public class TrabajadorRestController {

	@Autowired
	private TrabajadorService trabajadorService;
	
	
	//No usar: se bucla por la lista de citas de cada trabajador
	/*@GetMapping("/trabajadores")
	public List<Trabajador>leerTodos(){
		return trabajadorService.leerTodos();
	}*/
	
	
	
	@GetMapping("/trabajadores/{idTrabajador}")
	public ResponseEntity<?> buscarTrabajador(@PathVariable int idTrabajador) {
	    // 1. Buscar el trabajador (solo una vez)
	    Trabajador trabajador = trabajadorService.buscarUnTrabajador(idTrabajador);
	    
	    // 2. Verificar si existe
	    if (trabajador == null) {
	        return new ResponseEntity<String>("No hay ningún trabajador con ese Id", HttpStatusCode.valueOf(404));
	    } else {
	        // 3. Convertir a DTO y devolver
	        TrabajadorDTO trabajadorDTO = new TrabajadorDTO(trabajador);
	        return new ResponseEntity<TrabajadorDTO>(trabajadorDTO, HttpStatusCode.valueOf(200));
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
	
	@PutMapping("/trabajadores/{trabajadorId}")
	public ResponseEntity<?> actualizarTrabajador(@PathVariable int trabajadorId, @RequestBody Trabajador trabajador) {
	    try {
	        // Verificar que el trabajador existe
	        Trabajador trabajadorExistente = trabajadorService.buscarUnTrabajador(trabajadorId);
	        if (trabajadorExistente == null) {
	            return new ResponseEntity<>(
	                Map.of("mensaje", "No se encontró ningún trabajador con ID: " + trabajadorId),
	                HttpStatusCode.valueOf(404)
	            );
	        }

	        // Preservar las citas existentes y actualizar solo los otros campos
	        trabajadorExistente.setDni(trabajador.getDni());
	        trabajadorExistente.setNumeroCuenta(trabajador.getNumeroCuenta());
	        trabajadorExistente.setContrasenia(trabajador.getContrasenia());
	        trabajadorExistente.setNombre(trabajador.getNombre());
	        trabajadorExistente.setApellido1(trabajador.getApellido1());
	        trabajadorExistente.setApellido2(trabajador.getApellido2());
	        trabajadorExistente.setEmail(trabajador.getEmail());
	        trabajadorExistente.setTelefono(trabajador.getTelefono());
	        trabajadorExistente.setRol(trabajador.getRol());
	        trabajadorExistente.setFunciones(trabajador.getFunciones());
	        // Las citas se mantienen intactas

	        // Actualizar el trabajador
	        trabajadorService.actualizarTrabajador(trabajadorExistente);
	        
	        return new ResponseEntity<>(
	            Map.of("mensaje", "Trabajador actualizado correctamente"),
	            HttpStatusCode.valueOf(200)
	        );

	    } catch (Exception e) {
	        return new ResponseEntity<>(
	            Map.of("mensaje", "Error al actualizar el trabajador: " + e.getMessage()),
	            HttpStatusCode.valueOf(500)
	        );
	    }
	}
	
	@GetMapping("/rol/{rol}")
	public ResponseEntity<List<TrabajadorDTO>> obtenerTrabajadoresPorRol(@PathVariable String rol) {
	    try {
	        Rol rolEnum = Rol.valueOf(rol.toUpperCase());
	        List<TrabajadorDTO> trabajadores = trabajadorService.obtenerTrabajadoresPorRol(rolEnum);
	        return ResponseEntity.ok(trabajadores);
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().build(); // Rol inválido
	    }
	}
	
	
	
}
