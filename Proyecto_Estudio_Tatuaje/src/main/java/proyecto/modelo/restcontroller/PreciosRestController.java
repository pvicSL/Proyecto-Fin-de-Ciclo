package proyecto.modelo.restcontroller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.service.CitaService;

@RestController
@RequestMapping("/api/precios")
public class PreciosRestController {

    @Autowired
    private CitaService citaService;

    @GetMapping("/{idCita}")
    public ResponseEntity<?> obtenerPreciosIndividuales(@PathVariable int idCita) {
        PreciosIndividualesDTO precios = citaService.obtenerPreciosIndividualesPorCita(idCita);
        
        if (precios != null) {
            return ResponseEntity.ok(precios);
        } else {
            return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + idCita));
        }
    }
}