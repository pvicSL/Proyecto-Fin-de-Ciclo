package proyecto.modelo.restcontroller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import org.springframework.web.bind.annotation.RequestPart;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import proyecto.modelo.dto.CitaCompletaDTO;
import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.enums.Estatus;
import proyecto.service.CitaService;

@RestController
@RequestMapping("/api/citas")
@CrossOrigin(origins = "*")
public class CitaRestController {

	@Autowired
	private CitaService citaService;

	// NUEVO PARA IMG. REF.: Herramienta para convertir JSON String a Objeto Java
	@Autowired
	private ObjectMapper objectMapper;

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
		
		cita.setIdCita(id);

		Cita citaActualizada = citaService.actualizarCita(cita);

		if (citaActualizada != null) {
			return ResponseEntity.ok(new CitaDTO(citaActualizada));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	/*
	 * METODO CREAR CITA ANTIGUO, PARA REFERENCIA Y POR SI ACASO
	 * 
	 * @PostMapping("/crear-cita") public ResponseEntity<CitaDTO>
	 * crearCita(@RequestBody Cita cita) { Cita citaGuardada =
	 * citaService.crearCita(cita); CitaDTO citaDTO = new CitaDTO(citaGuardada);
	 * return ResponseEntity.ok(citaDTO); }
	 */

	// --- METODO CREAR CITA MODIFICADO PARA SUBIDA DE ARCHIVOS ---
	@PostMapping(value = "/crear-cita", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> crearCita(
			// Recibimos los datos de la cita como un String JSON
			@RequestPart("cita") String citaJson,
			// Recibimos la lista de archivos (puede ser null si el usuario no sube nada)
			@RequestPart(value = "ficheros", required = false) List<MultipartFile> ficheros) {

		try {
			// 1. Convertimos el String JSON a Objeto Cita
			Cita cita = objectMapper.readValue(citaJson, Cita.class);

			// 2. Llamamos al servicio pasando la cita Y los ficheros
			Cita citaGuardada = citaService.crearCita(cita, ficheros);

			CitaDTO citaDTO = new CitaDTO(citaGuardada);
			return ResponseEntity.ok(citaDTO);

		} catch (JsonProcessingException e) {
			return ResponseEntity.badRequest().body("Error al procesar el JSON de la cita: " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Error al guardar la cita o las imágenes");
		}
	}
	// ------------------------------------------------------------

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
	
    @GetMapping("/buscar/confirmadas")
    public ResponseEntity<?> obtenerCitasConfirmadas() {
        List<CitaDTO> citas = citaService.obtenerPorEstatus(Estatus.CONFIRMADO);
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas confirmadas"));
        }
        
        return ResponseEntity.ok(citas);
    }
    
    @GetMapping("/buscar/pendientes")
    public ResponseEntity<?> obtenerCitasPendientes() {
        List<CitaDTO> citas = citaService.obtenerPorEstatus(Estatus.PENDIENTE);
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas pendientes"));
        }
        
        return ResponseEntity.ok(citas);
    }
    
    @GetMapping("/detalles-completos/{id}")
    public ResponseEntity<?> obtenerDetallesCita(@PathVariable int id) {
        CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
        
        if (citaCompleta != null) {
            return ResponseEntity.ok(citaCompleta);
        } else {
            return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
        }
    }
	
    @PutMapping("/detalles-completos-modificar/{id}")
    public ResponseEntity<?> actualizarCitaCompleta(@PathVariable int id, @RequestBody CitaCompletaDTO citaEditada) {
        CitaCompletaDTO citaActualizada = citaService.actualizarCitaCompleta(id, citaEditada);
        
        if (citaActualizada != null) {
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Cita actualizada correctamente y presupuesto recalculado");
            respuesta.put("cita", citaActualizada);
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
        }
    }
    
    @GetMapping("/buscar/presupuesto-pendientes")
    public ResponseEntity<?> obtenerCitasPresupuestoPendiente() {
        List<CitaDTO> citas = citaService.obtenerPorEstadoPresupuesto(Estado.PENDIENTE);
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas con presupuesto pendiente"));
        }
        
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/buscar/presupuesto-generados")
    public ResponseEntity<?> obtenerCitasPresupuestoGenerado() {
        List<CitaDTO> citas = citaService.obtenerPorEstadoPresupuesto(Estado.GENERADO);
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas con presupuesto generado"));
        }
        
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/buscar/presupuesto-aceptados")
    public ResponseEntity<?> obtenerCitasPresupuestoAceptado() {
        List<CitaDTO> citas = citaService.obtenerPorEstadoPresupuesto(Estado.ACEPTADO);
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas con presupuesto aceptado"));
        }
        
        return ResponseEntity.ok(citas);
    }
	

	// Endpoint seguro para recuperar una cita
	// Recibe: ?ref=A5B6F1C2&email=patricia@email.com
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorReferencia(@RequestParam String ref, @RequestParam String email) {

        Optional<Cita> citaOpt = citaService.buscarPorReferenciaYEmail(ref, email);

        if (citaOpt.isPresent()) {
            Cita citaEncontrada = citaOpt.get();
            CitaDTO dto = new CitaDTO(citaEncontrada);
            
            // AQUI ESTA LA CLAVE: Calculamos la duración y la metemos en el DTO
            Integer duracion = citaService.calcularDuracion(citaEncontrada);
            dto.setDuracionEstimada(duracion);
            
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se encontró ninguna cita con esa referencia y email.");
        }
    }

	
    // Calcular duración sin guardar (Para el Formulario)
    @PostMapping("/calcular-duracion")
    public ResponseEntity<Integer> calcularDuracionEstimada(@RequestBody CitaDTO datos) {
        try {
            // Creamos una entidad temporal solo con los datos necesarios para el cálculo
            Cita citaSimulada = new Cita();
            
            // Convertimos los Strings del DTO a los Enums de la Entidad
            // Es vital usar los mismos nombres de ENUM que en Java (Mayúsculas)
            if (datos.getTamanio() != null) 
                citaSimulada.setTamanio(proyecto.modelo.enums.Tamanio.valueOf(datos.getTamanio()));
            
            if (datos.getDetalle() != null) 
                citaSimulada.setDetalle(proyecto.modelo.enums.Detalle.valueOf(datos.getDetalle())); // Ojo al mapa del front
            
            if (datos.getColoracion() != null) 
                citaSimulada.setColoracion(proyecto.modelo.enums.Coloracion.valueOf(datos.getColoracion()));

            // Usamos la lógica centralizada del servicio
            Integer minutos = citaService.calcularDuracion(citaSimulada);
            
            return ResponseEntity.ok(minutos);
            
        } catch (IllegalArgumentException e) {
            // Si el front manda un texto que no coincide con el Enum
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}
