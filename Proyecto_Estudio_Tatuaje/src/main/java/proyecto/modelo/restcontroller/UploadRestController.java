package proyecto.modelo.restcontroller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UploadRestController {

	
	@GetMapping("/verificar-imagen/{filename}")
	public ResponseEntity<Map<String, Object>> verificarImagen(@PathVariable String filename) {
	    Map<String, Object> response = new HashMap<>();
	    
	    try {
	        Path rutaArchivo = Paths.get("uploads/" + filename);
	        boolean existe = Files.exists(rutaArchivo);
	        
	        response.put("existe", existe);
	        response.put("filename", filename);
	        
	        if (existe) {
	            response.put("tamaño", Files.size(rutaArchivo));
	            response.put("url", "/uploads/" + filename);
	        }
	        
	        return ResponseEntity.ok(response);
	        
	    } catch (Exception e) {
	        response.put("existe", false);
	        response.put("error", e.getMessage());
	        return ResponseEntity.ok(response);
	    }
	}
	
	@GetMapping("/imagen-optimizada/{filename}")
	public ResponseEntity<Resource> servirImagenOptimizada(@PathVariable String filename) {
	    try {
	        Path rutaArchivo = Paths.get("uploads/" + filename);
	        Resource recurso = new UrlResource(rutaArchivo.toUri());
	        
	        return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_TYPE, "image/*")
	            .header(HttpHeaders.CACHE_CONTROL, "max-age=3600") // Cache 1 hora
	            .body(recurso);
	            
	    } catch (Exception e) {
	        return ResponseEntity.notFound().build();
	    }
	}
}
