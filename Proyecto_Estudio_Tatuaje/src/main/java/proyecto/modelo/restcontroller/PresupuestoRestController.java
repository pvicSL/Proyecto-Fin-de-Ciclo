package proyecto.modelo.restcontroller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.CitaPagoPublicoDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.PresupuestoRepository;
import proyecto.service.PresupuestoService;

@RestController
@RequestMapping("/api")
public class PresupuestoRestController {

	
	@Autowired
	private PresupuestoService presupuestoService;
	
	@Autowired
	private CitaRepository citaRepository;
	
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
	
	// --- BÚSQUEDA PÚBLICA PARA PASARELA DE PAGO ---
	@GetMapping("/buscar-publica/{referencia}")
	public ResponseEntity<?> buscarPorReferenciaPublica(@PathVariable String referencia) {
	    
	    // 1. Buscamos la entidad Cita mediante su referencia única
	    Optional<Cita> citaOpt = citaRepository.findByReferencia(referencia);

	    if (citaOpt.isPresent()) {
	        Cita cita = citaOpt.get();
	        
	        // 2. Instanciamos el NUEVO DTO que hereda del original
	        CitaPagoPublicoDTO dto = new CitaPagoPublicoDTO(cita);
	        
	        // 3. Buscamos el presupuesto asociado utilizando el ID de la cita
	        Optional<Presupuesto> presupuestoOpt = presupuestoRepository.findByIdServicio(cita.getIdCita());
	        
	        if (presupuestoOpt.isPresent()) {
	            Presupuesto presupuesto = presupuestoOpt.get();
	            // Si existe presupuesto, extraemos el precio final y lo insertamos en el DTO
	            dto.setPrecioTotal(presupuesto.getPrecioFinal());
	            // Asignamos la fianza requerida para confirmar la cita
	            dto.setFianza(new BigDecimal("30.00")); 
	        } else {
	            // Manejo de seguridad: si no hay presupuesto, enviamos valores a cero
	            // para evitar errores de tipo 'null' en el cliente.
	            dto.setPrecioTotal(BigDecimal.ZERO);
	            dto.setFianza(BigDecimal.ZERO);
	        }
	        
	        // Retornamos el DTO completo. Spring Boot lo transformará en un JSON plano.
	        return ResponseEntity.ok(dto);
	    } else {
	        return ResponseEntity.notFound().build();
	    }
	}

	
}
