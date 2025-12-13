package proyecto.modelo.restcontroller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.CitaDTO;
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
	            return ResponseEntity.notFound().build();  // 404 Not Found
	        }
	    }
}
