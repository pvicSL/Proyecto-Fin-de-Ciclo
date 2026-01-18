package proyecto.modelo.restcontroller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;

import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.modelo.entities.Precio;
import proyecto.service.CitaService;
import proyecto.service.PrecioService;

@RestController
@RequestMapping("/api/precios")
@CrossOrigin(origins = "*")
public class PreciosRestController {

    @Autowired
    private CitaService citaService;
    
    @Autowired
    private PrecioService precioService;

    @GetMapping("/{idCita}")
    public ResponseEntity<?> obtenerPreciosIndividuales(@PathVariable int idCita) {
        PreciosIndividualesDTO precios = citaService.obtenerPreciosIndividualesPorCita(idCita);
        
        if (precios != null) {
            return ResponseEntity.ok(precios);
        } else {
            return ResponseEntity.ok(Map.of("mensaje", "No se encontró la cita con ID: " + idCita));
        }
    }
    
    @GetMapping
    public ResponseEntity<?> leerTodos() {
        try {
            List<Precio> precios = precioService.leerTodos();
            
            if (precios.isEmpty()) {
                return ResponseEntity.ok(Map.of("mensaje", "No hay precios registrados"));
            } else {
                return ResponseEntity.ok(precios);
            }
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Error al obtener los precios: " + e.getMessage()));
        }
    }
    
    @PutMapping("/{idPrecio}")
    public ResponseEntity<?> actualizarPrecio(@PathVariable int idPrecio, @RequestBody Precio precio) {
        try {
            precio.setIdPrecio(idPrecio); // Asegurar que el ID coincida
            Precio precioActualizado = precioService.actualizarPrecio(precio);
            return ResponseEntity.ok(precioActualizado);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Error al actualizar el precio: " + e.getMessage()));
        }
    }
    
    @GetMapping("/detalle/{idPrecio}")
    public ResponseEntity<?> buscarUnPrecio(@PathVariable int idPrecio) {
        try {
            Precio precio = precioService.buscarUnPrecio(idPrecio);
            
            if (precio != null) {
                return ResponseEntity.ok(precio);
            } else {
                return ResponseEntity.ok(Map.of("mensaje", "No se encontró el precio con ID: " + idPrecio));
            }
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Error al buscar el precio: " + e.getMessage()));
        }
    }
    
    @PostMapping
    public ResponseEntity<?> altaPrecio(@RequestBody Precio precio) {
        try {
            Precio precioGuardado = precioService.altaPrecio(precio);
            return ResponseEntity.ok(precioGuardado);
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Error al crear el precio: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{idPrecio}")
    public ResponseEntity<?> eliminarPrecio(@PathVariable int idPrecio) {
        try {
            int resultado = precioService.eliminarPrecio(idPrecio);
            
            if (resultado == 1) {
                return ResponseEntity.ok(Map.of("mensaje", "Precio eliminado correctamente"));
            } else {
                return ResponseEntity.ok(Map.of("mensaje", "No se encontró el precio con ID: " + idPrecio));
            }
            
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Error al eliminar el precio: " + e.getMessage()));
        }
    }
    
    @PutMapping("/actualizar-base")
    public ResponseEntity<?> actualizarPrecioBase(@RequestBody BigDecimal nuevoPrecio) {
        try {
            // Validación
            if (nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.ok(Map.of("error", "El precio base no puede ser negativo"));
            }
            
            Precio precioBaseActualizado = precioService.actualizarPrecioBase(nuevoPrecio);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Precio base actualizado correctamente",
                "precio", precioBaseActualizado
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Error al actualizar el precio base: " + e.getMessage()));
        }
    }
}