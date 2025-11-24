package proyecto.modelo.restcontroller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.entities.Cliente;
import proyecto.service.ClienteService;

@RestController
public class ClienteRestController {

	@Autowired
	ClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente>leerTodos() {
		return clienteService.leerTodos();
	}
	
}
