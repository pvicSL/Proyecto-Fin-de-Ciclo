package proyecto.modelo.restcontroller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import proyecto.modelo.entities.Cita;
import proyecto.service.CitaService;

@RestController
@RequestMapping("/api/citas")
public class CitaRestController {

	@Autowired
	private CitaService citaService;

	@GetMapping
	public ResponseEntity<List<CitaDTO>> listarCitas() {
		// Service ya devuelve DTOs directamente
		List<CitaDTO> citasDTO = citaService.listarCitasDTO();
		return ResponseEntity.ok(citasDTO);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CitaDTO> obtenerCita(@PathVariable int id) {
		// Service maneja la conversión y validación
		CitaDTO citaDTO = citaService.obtenerCitaDTOPorId(id);

		if (citaDTO != null) {
			return ResponseEntity.ok(citaDTO);
		} else {
			return ResponseEntity.notFound().build(); // 404 Not Found
		}
	}

	@PutMapping("/actualizar/{id}")
	public ResponseEntity<CitaDTO> actualizarCita(@PathVariable int id, @RequestBody Cita cita) {
		// Nos aseguramos de que el ID del objeto coincida con el de la URL
		cita.setIdCita(id);

		Cita citaActualizada = citaService.actualizarCita(cita);

		if (citaActualizada != null) {
			return ResponseEntity.ok(new CitaDTO(citaActualizada));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping("/crear-cita")
	public ResponseEntity<CitaDTO> crearCita(@RequestBody Cita cita) {
		Cita citaGuardada = citaService.crearCita(cita);
		CitaDTO citaDTO = new CitaDTO(citaGuardada);
		return ResponseEntity.ok(citaDTO);
	}

	@GetMapping("/disponibilidad/{duracion}")
	public ResponseEntity<Map<String, List<String>>> obtenerDisponibilidad(@PathVariable int duracion) {
		// Llama al servicio que acabamos de crear
		Map<String, List<String>> huecos = citaService.buscarHuecosDisponibles(duracion);

		return ResponseEntity.ok(huecos);
	}

	@DeleteMapping("/eliminar/{id}")
	public ResponseEntity<String> eliminarCita(@PathVariable int id) {
		int resultado = citaService.eliminarCita(id);

		if (resultado == 1) {
			return ResponseEntity.ok("Cita eliminada correctamente");
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la cita con ID: " + id);
		}
	}

}
