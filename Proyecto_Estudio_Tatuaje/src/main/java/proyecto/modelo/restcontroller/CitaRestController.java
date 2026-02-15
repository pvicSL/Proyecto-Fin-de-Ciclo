package proyecto.modelo.restcontroller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

import jakarta.transaction.Transactional;
import proyecto.modelo.dto.CitaCompletaDTO;
import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.CitaModificacionDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.enums.Coloracion;
import proyecto.modelo.enums.Detalle;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.enums.Estatus;
import proyecto.modelo.enums.Estilo;
import proyecto.modelo.enums.Tamanio;
import proyecto.modelo.enums.Tipo;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.PresupuestoRepository;
import proyecto.security.SecurityUtils;
import proyecto.service.CitaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/citas")
public class CitaRestController {

	@Autowired
	private CitaService citaService;
	
	@Autowired
	private CitaRepository citaRepository;
	
	@Autowired
	private PresupuestoRepository presupuestoRepository;

	// NUEVO PARA IMG. REF.: Herramienta para convertir JSON String a Objeto Java
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private SecurityUtils securityUtils;

	@GetMapping("/listarCitas") //SEGURIDAD OK
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        
        // Si está autenticado, aplicar filtros por rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            if (securityUtils.esAdmin()) {
                // Admin ve todas
                List<CitaDTO> citasDTO = citaService.listarCitasDTO();
                return ResponseEntity.ok(citasDTO);
            } else {
                // Trabajador ve solo las suyas
                Integer idTrabajador = securityUtils.obtenerIdTrabajador();
                List<CitaDTO> citasDTO = citaService.listarCitasDelTrabajador(idTrabajador);
                return ResponseEntity.ok(citasDTO);
            }
        }
        
        // Sin autenticación: comportamiento normal para testing
        List<CitaDTO> citasDTO = citaService.listarCitasDTO();
        return ResponseEntity.ok(citasDTO);
    }

	@GetMapping("/{idCita}")	//SEGURIDAD OK
	public ResponseEntity<CitaDTO> obtenerCita(@PathVariable int idCita) {
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin puede ver cualquier cita
	            CitaDTO citaDTO = citaService.obtenerCitaDTOPorId(idCita);
	            if (citaDTO != null) {
	                return ResponseEntity.ok(citaDTO);
	            } else {
	                return ResponseEntity.notFound().build();
	            }
	        } else {
	            // Trabajador: verificar que la cita le pertenece
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            CitaDTO citaDTO = citaService.obtenerCitaDTOPorId(idCita);
	            
	            if (citaDTO != null) {
	                // Verificar si la cita pertenece al trabajador
	                List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	                boolean esSuya = susCitas.stream()
	                    .anyMatch(suya -> suya.getIdCita() == idCita);
	                
	                if (esSuya) {
	                    return ResponseEntity.ok(citaDTO);
	                } else {
	                    return ResponseEntity.notFound().build(); // No revelar que existe
	                }
	            } else {
	                return ResponseEntity.notFound().build();
	            }
	        }
	    }
	    
	    // Sin autenticación: comportamiento normal para testing
	    CitaDTO citaDTO = citaService.obtenerCitaDTOPorId(idCita);
	    if (citaDTO != null) {
	        return ResponseEntity.ok(citaDTO);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	@PutMapping("/actualizar/{idCita}")		//SEGURIDAD OK
	public ResponseEntity<CitaDTO> actualizarCita(@PathVariable int idCita, @RequestBody Cita cita) {
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin puede actualizar cualquier cita
	            cita.setIdCita(idCita);
	            Cita citaActualizada = citaService.actualizarCita(cita);
	            if (citaActualizada != null) {
	                return ResponseEntity.ok(new CitaDTO(citaActualizada));
	            } else {
	                return ResponseEntity.notFound().build();
	            }
	        } else {
	            // Trabajador: verificar que la cita le pertenece
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            boolean esSuya = susCitas.stream()
	                .anyMatch(suya -> suya.getIdCita() == idCita);
	            
	            if (esSuya) {
	                cita.setIdCita(idCita);
	                Cita citaActualizada = citaService.actualizarCita(cita);
	                if (citaActualizada != null) {
	                    return ResponseEntity.ok(new CitaDTO(citaActualizada));
	                } else {
	                    return ResponseEntity.notFound().build();
	                }
	            } else {
	                return ResponseEntity.notFound().build();
	            }
	        }
	    }
	    
	    // Sin autenticación: comportamiento normal para testing
	    cita.setIdCita(idCita);
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
			//SEGURIDAD OK
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
	
	@GetMapping("/disponibilidad/{duracion}/{idTrabajador}")	//SEGURIDAD OK
	public ResponseEntity<Map<String, List<String>>> obtenerDisponibilidad(
	    @PathVariable int duracion, 
	    @PathVariable int idTrabajador) {
	    
	    Map<String, List<String>> huecos = citaService.buscarHuecosDisponibles(duracion, idTrabajador);
	    return ResponseEntity.ok(huecos);
	}

	@DeleteMapping("/eliminar/{idCita}")	//SEGURIDAD OK
	public ResponseEntity<String> eliminarCita(@PathVariable int idCita) {
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin puede eliminar cualquier cita
	            int resultado = citaService.eliminarCita(idCita);
	            if (resultado == 1) {
	                return ResponseEntity.ok("Cita eliminada correctamente");
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la cita con ID: " + idCita);
	            }
	        } else {
	            // Trabajador: verificar que la cita le pertenece antes de eliminar
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            boolean esSuya = susCitas.stream()
	                .anyMatch(suya -> suya.getIdCita() == idCita);
	            
	            if (esSuya) {
	                int resultado = citaService.eliminarCita(idCita);
	                if (resultado == 1) {
	                    return ResponseEntity.ok("Cita eliminada correctamente");
	                } else {
	                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la cita con ID: " + idCita);
	                }
	            } else {
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la cita con ID: " + idCita);
	            }
	        }
	    }
	    
	    // Sin autenticación: comportamiento normal para testing
	    int resultado = citaService.eliminarCita(idCita);
	    if (resultado == 1) {
	        return ResponseEntity.ok("Cita eliminada correctamente");
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró la cita con ID: " + idCita);
	    }
	}
	
	@GetMapping("/buscar/confirmadas/{fecha}/{vista}")
	public ResponseEntity<List<CitaDTO>> obtenerPorRango(
	    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
	    @PathVariable String vista) {
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin ve todas por rango
	            return ResponseEntity.ok(citaService.obtenerPorRango(fecha, vista));
	        } else {
	            // Trabajador: filtrar solo las suyas del rango
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> todasDelRango = citaService.obtenerPorRango(fecha, vista);
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            
	            // Intersección: citas que están en el rango Y son suyas
	            List<CitaDTO> susCitasDelRango = todasDelRango.stream()
	                .filter(rango -> susCitas.stream()
	                    .anyMatch(suya -> suya.getIdCita() == rango.getIdCita()))
	                .collect(Collectors.toList());
	                
	            return ResponseEntity.ok(susCitasDelRango);
	        }
	    }
	    
	    // Sin autenticación: comportamiento normal
	    return ResponseEntity.ok(citaService.obtenerPorRango(fecha, vista));
	}
	
	@GetMapping("/buscar/confirmadas") //SEGURIDAD OK
	public ResponseEntity<?> obtenerCitasConfirmadas() {
	    List<CitaDTO> citas;
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin ve todas las confirmadas
	            citas = citaService.obtenerPorEstatus(Estatus.CONFIRMADO);
	        } else {
	            // Trabajador: obtener SUS citas y filtrar las confirmadas
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            
	            // Filtrar solo las confirmadas
	            citas = susCitas.stream()
	                .filter(cita -> "CONFIRMADO".equals(cita.getEstatus()))  // ← Filtro en Java
	                .collect(Collectors.toList());
	        }
	    } else {
	        // Sin autenticación: comportamiento normal
	        citas = citaService.obtenerPorEstatus(Estatus.CONFIRMADO);
	    }
	    
	    if (citas.isEmpty()) {
	        return ResponseEntity.ok(Map.of("mensaje", "No hay citas confirmadas"));
	    }
	    return ResponseEntity.ok(citas);
	}
    
	@GetMapping("/buscar/pendientes") //SEGURIDAD OK
	public ResponseEntity<?> obtenerCitasPendientes() {
	    List<CitaDTO> citas;
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin ve todas las pendientes
	            citas = citaService.obtenerPorEstatus(Estatus.PENDIENTE);
	        } else {
	            // Trabajador: obtener SUS citas y filtrar las pendientes
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            
	            // Filtrar solo las pendientes
	            citas = susCitas.stream()
	                .filter(cita -> "PENDIENTE".equals(cita.getEstatus()))
	                .collect(Collectors.toList());
	        }
	    } else {
	        // Sin autenticación: comportamiento normal
	        citas = citaService.obtenerPorEstatus(Estatus.PENDIENTE);
	    }
	    
	    if (citas.isEmpty()) {
	        return ResponseEntity.ok(Map.of("mensaje", "No hay citas pendientes"));
	    }
	    return ResponseEntity.ok(citas);
	}
    
	@GetMapping("/detalles-completos/{id}") //SEGURIDAD OK
	public ResponseEntity<?> obtenerDetallesCita(@PathVariable int id) {
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin puede ver detalles de cualquier cita
	            CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
	            if (citaCompleta != null) {
	                return ResponseEntity.ok(citaCompleta);
	            } else {
	                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	            }
	        } else {
	            // Trabajador: verificar que la cita le pertenece
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            boolean esSuya = susCitas.stream()
	                .anyMatch(suya -> suya.getIdCita() == id);
	            
	            if (esSuya) {
	                CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
	                if (citaCompleta != null) {
	                    return ResponseEntity.ok(citaCompleta);
	                } else {
	                    return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	                }
	            } else {
	                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	            }
	        }
	    }
	    
	    // Sin autenticación: comportamiento normal para testing
	    CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
	    if (citaCompleta != null) {
	        return ResponseEntity.ok(citaCompleta);
	    } else {
	        return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	    }
	}
	
	@PutMapping("/detalles-completos-modificar/{id}")	//SEGURIDAD OK
	public ResponseEntity<?> actualizarCitaCompleta(@PathVariable int id, @RequestBody CitaCompletaDTO citaEditada) {
	    
	    // Si está autenticado, aplicar filtros por rol
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
	        
	        if (securityUtils.esAdmin()) {
	            // Admin puede modificar cualquier cita
	            CitaCompletaDTO citaActualizada = citaService.actualizarCitaCompleta(id, citaEditada);
	            if (citaActualizada != null) {
	                Map<String, Object> respuesta = new HashMap<>();
	                respuesta.put("mensaje", "Cita actualizada correctamente y presupuesto recalculado");
	                respuesta.put("cita", citaActualizada);
	                return ResponseEntity.ok(respuesta);
	            } else {
	                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	            }
	        } else {
	            // Trabajador: verificar que la cita le pertenece
	            Integer idTrabajador = securityUtils.obtenerIdTrabajador();
	            List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
	            boolean esSuya = susCitas.stream()
	                .anyMatch(suya -> suya.getIdCita() == id);
	            
	            if (esSuya) {
	                CitaCompletaDTO citaActualizada = citaService.actualizarCitaCompleta(id, citaEditada);
	                if (citaActualizada != null) {
	                    Map<String, Object> respuesta = new HashMap<>();
	                    respuesta.put("mensaje", "Cita actualizada correctamente y presupuesto recalculado");
	                    respuesta.put("cita", citaActualizada);
	                    return ResponseEntity.ok(respuesta);
	                } else {
	                    return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	                }
	            } else {
	                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
	            }
	        }
	    }
	    
	    // Sin autenticación: comportamiento normal para testing
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
    
    @GetMapping("/buscar/presupuesto-pendientes") //SEGURIDAD OK
    public ResponseEntity<?> obtenerCitasPresupuestoPendiente() {
        List<CitaDTO> citas;
        
        // Si está autenticado, aplicar filtros por rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            if (securityUtils.esAdmin()) {
                // Admin ve todas con presupuesto pendiente
                citas = citaService.obtenerPorEstadoPresupuesto(Estado.PENDIENTE);
            } else {
                // Trabajador: intersección de SUS citas con las que tienen presupuesto pendiente
                Integer idTrabajador = securityUtils.obtenerIdTrabajador();
                List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
                List<CitaDTO> todasPendientes = citaService.obtenerPorEstadoPresupuesto(Estado.PENDIENTE);
                
                // Intersección: citas que están en ambas listas
                citas = susCitas.stream()
                    .filter(suya -> todasPendientes.stream()
                        .anyMatch(pendiente -> suya.getIdCita() == pendiente.getIdCita()))
                    .collect(Collectors.toList());
            }
        } else {
            // Sin autenticación: comportamiento normal
            citas = citaService.obtenerPorEstadoPresupuesto(Estado.PENDIENTE);
        }
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas con presupuesto pendiente"));
        }
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/buscar/presupuesto-generados") //SEGURIDAD OK
    public ResponseEntity<?> obtenerCitasPresupuestoGenerado() {
        List<CitaDTO> citas;
        
        // Si está autenticado, aplicar filtros por rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            if (securityUtils.esAdmin()) {
                // Admin ve todas con presupuesto generado
                citas = citaService.obtenerPorEstadoPresupuesto(Estado.GENERADO);
            } else {
                // Trabajador: intersección de SUS citas con las que tienen presupuesto generado
                Integer idTrabajador = securityUtils.obtenerIdTrabajador();
                List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
                List<CitaDTO> todasGeneradas = citaService.obtenerPorEstadoPresupuesto(Estado.GENERADO);
                
                // Intersección: citas que están en ambas listas
                citas = susCitas.stream()
                    .filter(suya -> todasGeneradas.stream()
                        .anyMatch(generada -> suya.getIdCita() == generada.getIdCita()))
                    .collect(Collectors.toList());
            }
        } else {
            // Sin autenticación: comportamiento normal
            citas = citaService.obtenerPorEstadoPresupuesto(Estado.GENERADO);
        }
        
        if (citas.isEmpty()) {
            return ResponseEntity.ok(Map.of("mensaje", "No hay citas con presupuesto generado"));
        }
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/buscar/presupuesto-aceptados") //SEGURIDAD OK
    public ResponseEntity<?> obtenerCitasPresupuestoAceptado() {
        List<CitaDTO> citas;
        
        // Si está autenticado, aplicar filtros por rol
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            if (securityUtils.esAdmin()) {
                // Admin ve todas con presupuesto aceptado
                citas = citaService.obtenerPorEstadoPresupuesto(Estado.ACEPTADO);
            } else {
                // Trabajador: intersección de SUS citas con las que tienen presupuesto aceptado
                Integer idTrabajador = securityUtils.obtenerIdTrabajador();
                List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
                List<CitaDTO> todasAceptadas = citaService.obtenerPorEstadoPresupuesto(Estado.ACEPTADO);
                
                // Intersección: citas que están en ambas listas
                citas = susCitas.stream()
                    .filter(suya -> todasAceptadas.stream()
                        .anyMatch(aceptada -> suya.getIdCita() == aceptada.getIdCita()))
                    .collect(Collectors.toList());
            }
        } else {
            // Sin autenticación: comportamiento normal
            citas = citaService.obtenerPorEstadoPresupuesto(Estado.ACEPTADO);
        }
        
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
    
    @PutMapping("/modificar-conreferencia")
    public ResponseEntity<?> modificarCitaSegura(@RequestBody CitaModificacionDTO solicitud) {
        boolean exito = citaService.modificarFechaCita(solicitud);

        if (exito) {
            return ResponseEntity.ok(Map.of("mensaje", "Cita modificada correctamente."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "No se encontró la cita o el email no coincide."));
        }
    }

    @DeleteMapping("/cancelar-conreferencia")
    public ResponseEntity<?> cancelarCitaSegura(
            @RequestParam String ref, 
            @RequestParam String email) {
        
        boolean exito = citaService.cancelarCitaPorReferencia(ref, email);

        if (exito) {
            return ResponseEntity.ok("Cita cancelada correctamente.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No se pudo cancelar: Cita no encontrada o credenciales inválidas.");
        }
    }
    

    
 

 // Asignar trabajador a una cita
    @PostMapping("/{citaId}/asignar-trabajador/{trabajadorId}")
    public ResponseEntity<?> asignarTrabajador(@PathVariable int citaId, @PathVariable int trabajadorId) {
        String resultado = citaService.asignarTrabajador(citaId, trabajadorId);
        
        // Si el resultado empieza con "Error:", devolvemos status de error
        if (resultado.startsWith("Error:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", resultado));
        } else {
            return ResponseEntity.ok(Map.of("mensaje", resultado));
        }
    }

    // Desasignar trabajador de una cita  
    @DeleteMapping("/{citaId}/desasignar-trabajador")
    public ResponseEntity<?> desasignarTrabajador(@PathVariable int citaId) {
        String resultado = citaService.desasignarTrabajador(citaId);
        
        // Si el resultado empieza con "Error:", devolvemos status de error
        if (resultado.startsWith("Error:")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", resultado));
        } else {
            return ResponseEntity.ok(Map.of("mensaje", resultado));
        }
    }
    
    @PutMapping("/{id}/presupuesto/aceptar")	//Seguridad ok
    public ResponseEntity<?> aceptarPresupuesto(@PathVariable int id) {
        
        // Si está autenticado, verificar que es trabajador Y que es el asignado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            if (securityUtils.esAdmin()) {
                // Admin NO puede aceptar presupuestos, solo trabajadores
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo el trabajador asignado puede aceptar presupuestos"));
            } else {
                // Trabajador: verificar que la cita le está asignada
                Integer idTrabajador = securityUtils.obtenerIdTrabajador();
                List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
                boolean esSuya = susCitas.stream()
                    .anyMatch(suya -> suya.getIdCita() == id);
                
                if (!esSuya) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Solo el trabajador asignado puede aceptar este presupuesto"));
                }
                
                // Es su cita, puede aceptar el presupuesto
                try {
                    Cita cita = citaRepository.findById(id).orElse(null);
                    if (cita == null) {
                        return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
                    }
                    
                    citaService.aceptarPresupuesto(cita);
                    CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
                    
                    if (citaCompleta != null) {
                        return ResponseEntity.ok(citaCompleta);
                    } else {
                        return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
                    }
                    
                } catch (IllegalArgumentException | IllegalStateException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", e.getMessage()));
                }
            }
        }
        
        // Sin autenticación: comportamiento normal para testing
        try {
            Cita cita = citaRepository.findById(id).orElse(null);
            if (cita == null) {
                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
            }
            
            citaService.aceptarPresupuesto(cita);
            CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
            
            if (citaCompleta != null) {
                return ResponseEntity.ok(citaCompleta);
            } else {
                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/presupuesto/rechazar") //SEGURIDAD OK
    public ResponseEntity<?> rechazarPresupuesto(@PathVariable int id) {
        
        // Si está autenticado, verificar que es trabajador Y que es el asignado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            
            if (securityUtils.esAdmin()) {
                // Admin NO puede rechazar presupuestos, solo trabajadores
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Solo el trabajador asignado puede rechazar presupuestos"));
            } else {
                // Trabajador: verificar que la cita le está asignada
                Integer idTrabajador = securityUtils.obtenerIdTrabajador();
                List<CitaDTO> susCitas = citaService.listarCitasDelTrabajador(idTrabajador);
                boolean esSuya = susCitas.stream()
                    .anyMatch(suya -> suya.getIdCita() == id);
                
                if (!esSuya) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Solo el trabajador asignado puede rechazar este presupuesto"));
                }
                
                // Es su cita, puede rechazar el presupuesto
                try {
                    Cita cita = citaRepository.findById(id).orElse(null);
                    if (cita == null) {
                        return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
                    }
                    
                    // Rechazar presupuesto
                    citaService.rechazarPresupuesto(cita);
                    
                    // Desasignar trabajador
                    String resultadoDesasignacion = citaService.desasignarTrabajador(id);
                    if (resultadoDesasignacion.startsWith("Error:")) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(Map.of("error", resultadoDesasignacion));
                    }
                    
                    CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
                    
                    if (citaCompleta != null) {
                        return ResponseEntity.ok(citaCompleta);
                    } else {
                        return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
                    }
                    
                } catch (IllegalArgumentException | IllegalStateException e) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(Map.of("error", e.getMessage()));
                }
            }
        }
        
        // Sin autenticación: comportamiento normal para testing
        try {
            Cita cita = citaRepository.findById(id).orElse(null);
            if (cita == null) {
                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
            }
            
            citaService.rechazarPresupuesto(cita);
            String resultadoDesasignacion = citaService.desasignarTrabajador(id);
            
            if (resultadoDesasignacion.startsWith("Error:")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", resultadoDesasignacion));
            }
            
            CitaCompletaDTO citaCompleta = citaService.obtenerCitaCompleta(id);
            
            if (citaCompleta != null) {
                return ResponseEntity.ok(citaCompleta);
            } else {
                return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + id));
            }
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
 // Endpoint intermedio: Recibe los datos del formulario y devuelve:
    // 1. Duración exacta.
    // 2. ID del Trabajador más adecuado.
    @PostMapping("/calculo-previo")
    public ResponseEntity<?> calcularDatosPrevios(@RequestBody CitaDTO criterios) {
        try {
            // 1. Calcular duración
            // Usamos una entidad temporal para aprovechar tu método 'calcularDuracion'
            Cita citaTemp = new Cita();
            // Mapeamos los Enums desde los Strings del DTO
            if(criterios.getTamanio() != null) citaTemp.setTamanio(Tamanio.valueOf(criterios.getTamanio()));
            if(criterios.getDetalle() != null) citaTemp.setDetalle(Detalle.valueOf(criterios.getDetalle()));
            if(criterios.getColoracion() != null) citaTemp.setColoracion(Coloracion.valueOf(criterios.getColoracion()));
            
            Integer duracion = citaService.calcularDuracion(citaTemp);

            // 2. Seleccionar trabajador
            Tipo tipoServicio = Tipo.valueOf(criterios.getTipo());
            Estilo estiloServicio = Estilo.valueOf(criterios.getEstilo());
            
            Trabajador trabajador = citaService.seleccionarTrabajadorAutomatico(tipoServicio, estiloServicio);

            // 3. Devolver respuesta combinada
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("duracion", duracion);
            respuesta.put("idTrabajador", trabajador.getIdTrabajador());
            
            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error calculando datos previos: " + e.getMessage());
        }
    }
    
    	@GetMapping("/huecos-disponibles")
    	public ResponseEntity<?> getHuecos(@RequestParam int duracion, @RequestParam int idTrabajador) {
    		return ResponseEntity.ok(citaService.buscarHuecosDisponibles(duracion, idTrabajador));
    }
    	
    	@PostMapping("/confirmar-pago/{referencia}")
    	public ResponseEntity<?> confirmarPago(@PathVariable String referencia) {
    	    Optional<Cita> citaOpt = citaRepository.findByReferencia(referencia);

    	    if (citaOpt.isPresent()) {
    	        Cita cita = citaOpt.get();
    	        
    	        // 1. BLOQUEOS DE SEGURIDAD
    	        if (cita.getEstatus() == Estatus.CONFIRMADO) {
    	                return ResponseEntity.badRequest().body("Error: Esta cita ya ha sido pagada previamente.");
    	        }
    	        
    	        if (cita.getFechaLimitePago() != null && LocalDateTime.now().isAfter(cita.getFechaLimitePago())) {
    	            return ResponseEntity.status(HttpStatus.GONE).body("Error: El enlace ha caducado.");
    	        }

    	        // 2. ACTUALIZAR ESTADO DE LA CITA
    	        cita.setEstatus(Estatus.CONFIRMADO);
    	        cita.setFechaLimitePago(null); 
    	        citaRepository.save(cita);
    	        
    	        // 3. ACTUALIZAR ESTADO DEL PRESUPUESTO (EL BLOQUE QUE FALTABA)
    	        // Buscamos el presupuesto asociado a esta cita
    	        Optional<Presupuesto> presOpt = presupuestoRepository.findByIdServicio(cita.getIdCita());
    	        
    	        if (presOpt.isPresent()) {
    	            Presupuesto presupuesto = presOpt.get();
    	            // Solo cambiamos a ACEPTADO si estaba en GENERADO (para mantener coherencia)
    	            if (presupuesto.getEstado() == Estado.GENERADO) {
    	                presupuesto.setEstado(Estado.ACEPTADO);
    	                presupuestoRepository.save(presupuesto);
    	            }
    	        }
    	        
    	        return ResponseEntity.ok(java.util.Map.of("mensaje", "Pago registrado, cita confirmada y presupuesto aceptado."));
    	    } else {
    	        return ResponseEntity.notFound().build();
    	    }
    	}
    	
        // --- NUEVO: BUSQUEDA PÚBLICA PARA PASARELA DE PAGO ---
        @GetMapping("/buscar-publica/{referencia}")
        public ResponseEntity<?> buscarPorReferenciaPublica(@PathVariable String referencia) {
            // Usamos el repositorio directamente para buscar por la referencia única
            Optional<Cita> citaOpt = citaRepository.findByReferencia(referencia);

            if (citaOpt.isPresent()) {
                Cita cita = citaOpt.get();
                CitaDTO dto = new CitaDTO(cita);
                
                // Si tu DTO no mapea automáticamente el precio del presupuesto, 
                // asegúrate de setearlo aquí si fuera necesario, o de que CitaDTO ya lo incluya.
                // dto.setPrecioFinal( ... ); 
                
                return ResponseEntity.ok(dto);
            } else {
                return ResponseEntity.notFound().build();
            }
        }

     // Endpoint para el cierre operativo
        @PutMapping("/{id}/finalizar-trabajo")
        public ResponseEntity<?> finalizarTrabajo(@PathVariable Integer id) {
        citaService.finalizarTrabajo(id);
        return ResponseEntity.ok().build();
        }
        // Endpoint para la factura confirmada manualmente
        @PutMapping("/{id}/generar-factura")
        public ResponseEntity<?> generarFacturaManual(@PathVariable Integer id) {
        citaService.generarFacturaManual(id);
        return ResponseEntity.ok().build();
        }

        
        @GetMapping("/api/admin/facturas/realizadas")
        public ResponseEntity<List<CitaCompletaDTO>> obtenerFacturasRealizadas() {
            List<CitaCompletaDTO> facturas = citaService.obtenerFacturasRealizadas();
            return ResponseEntity.ok(facturas);
        }

}
