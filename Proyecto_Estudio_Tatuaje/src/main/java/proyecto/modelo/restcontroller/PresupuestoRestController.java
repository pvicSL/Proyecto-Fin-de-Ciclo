package proyecto.modelo.restcontroller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.CitaPresupuestoDTO;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.repository.PresupuestoRepository;
import proyecto.service.PresupuestoService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class PresupuestoRestController {

	
	@Autowired
	private PresupuestoService presupuestoService;
	
	@Autowired 
	private PresupuestoRepository presupuestoRepository;
	
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
	
	@GetMapping("/buscar-presupuesto/{idCita}")
	public ResponseEntity<?> obtenerPresupuestoPorCita(@PathVariable int idCita) {
	    try {
	        // Llamamos al método del service que implementamos antes
	        Presupuesto presupuesto = presupuestoService.buscarUnPresupuestoPorIdCita(idCita);
	        return ResponseEntity.ok(presupuesto);
	    } catch (NoSuchElementException e) {
	        // Si no hay presupuesto para esa cita, devolvemos 404
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                             .body("Todavía no existe un presupuesto para la cita " + idCita);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
	
	/**
     * Busca el presupuesto asociado a una cita y lo actualiza
     * URL: PUT /api/presupuestos/recalcular/5
     */
	@PutMapping("/actualizar-generar/{idCita}")
	public ResponseEntity<?> actualizarConExtra(@PathVariable int idCita, @RequestBody Presupuesto presupuestoFront) {
	    try {
	        // 1. Buscamos el presupuesto real en la base de datos por el id de la cita
	        Presupuesto presupuestoBD = presupuestoRepository.findByIdServicio(idCita)
	                .orElseThrow(() -> new RuntimeException("No existe presupuesto para la cita: " + idCita));

	        // 2. Seteamos el nuevo precio extra y comentarios que viene del Body de Postman
	        presupuestoBD.setPrecioExtra(presupuestoFront.getPrecioExtra());
	        presupuestoBD.setComentarios(presupuestoFront.getComentarios());

	        // 3. Llamamos al service para recalcular todo con el nuevo extra
	        Presupuesto actualizado = presupuestoService.actualizarPresupuesto(presupuestoBD);
	        
	        return ResponseEntity.ok(actualizado);
	    } catch (Exception e) {
	        // Esto te imprimirá en la consola de Spring el error real (mira el log)
	        e.printStackTrace(); 
	        return ResponseEntity.status(500).body("Error: " + e.getMessage());
	    }
	}
	
	@GetMapping("/estado/{estado}")
	public ResponseEntity<List<CitaPresupuestoDTO>> getCitasByEstadoPresupuesto(@PathVariable Estado estado) {
	    // Al poner 'Estado estado', Spring busca el String en el Enum automáticamente
	    List<CitaPresupuestoDTO> lista = presupuestoService.obtenerCitasPorEstadoPresupuesto(estado);
	    
	    if (lista.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }
	    
	    return ResponseEntity.ok(lista);
    }
	
	
}
