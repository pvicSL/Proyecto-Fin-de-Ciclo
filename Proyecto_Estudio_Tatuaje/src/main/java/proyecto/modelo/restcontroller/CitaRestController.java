package proyecto.modelo.restcontroller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.CitaAdminDTO;
import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.Estatus;
import proyecto.service.CitaService;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
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
	
	@GetMapping("/buscar/confirmadas/{fecha}/{vista}")
	public ResponseEntity<List<CitaDTO>> obtenerPorRango(
		@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha, 
		@PathVariable String vista) {
	    
	    return ResponseEntity.ok(citaService.obtenerPorRango(fecha, vista));
	}
	
	@GetMapping("/buscar/pendientes")
	public ResponseEntity<List<CitaDTO>> getCitasPendientes() {
	    // Llamamos al servicio para obtener la lista
	    List<CitaDTO> pendientes = citaService.obtenerPorEstatus(Estatus.PENDIENTE);

	    if (pendientes.isEmpty()) {
	        // Retornamos 204 No Content si la lista está vacía (opcional, también puedes devolver 200 con lista vacía)
	        return ResponseEntity.noContent().build();
	    }

	    // Retornamos 200 OK con la lista de citas
	    return ResponseEntity.ok(pendientes);
	}
	
	/**
     * Endpoint para obtener el detalle completo de una cita para el administrador.
     */
    @GetMapping("/detalle/{id}")
    public ResponseEntity<?> obtenerDetalleCita(@PathVariable("id") int idServicio) {
        try {
            CitaAdminDTO dto = citaService.obtenerDetalleCita(idServicio);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            // Si no encuentra la cita o el presupuesto, devuelve un 404
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Error genérico del servidor
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

}
