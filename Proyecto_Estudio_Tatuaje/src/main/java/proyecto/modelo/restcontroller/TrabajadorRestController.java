package proyecto.modelo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.entities.Trabajador;
import proyecto.service.TrabajadorService;

@RestController
public class TrabajadorRestController {

	@Autowired
	private TrabajadorService trabajadorService;
	
	
	@GetMapping("/trabajadores")
	public List<Trabajador>leerTodos(){
		return trabajadorService.leerTodos();
	}
	
	
	
}
