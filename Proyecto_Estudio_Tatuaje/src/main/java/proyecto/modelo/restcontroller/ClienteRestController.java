package proyecto.modelo.restcontroller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import proyecto.modelo.entities.Cliente;
import proyecto.service.ClienteService;


@RestController
public class ClienteRestController {

	@Autowired
	private ClienteService clienteService;
	
	@GetMapping("/clientes")
	public List<Cliente>leerTodos() {
		return clienteService.leerTodos();
	}
	
	@GetMapping("cliente/{idCliente}")
	public ResponseEntity<?>buscarCliente(@PathVariable int idCliente) {
		if (clienteService.buscarUnCliente(idCliente) == null) {
			return new ResponseEntity<String>("No hay ningún cliente con ese Id", HttpStatusCode.valueOf(404));
		} else {
			return new ResponseEntity<Cliente>(clienteService.buscarUnCliente(idCliente), HttpStatusCode.valueOf(200));
		}
	}
	
	@PostMapping("/cliente-alta")
	public ResponseEntity<?>altaCliente(@RequestBody Cliente cliente) {
		try {
			Cliente confirmacionCliente = clienteService.altaCliente(cliente);
			return new ResponseEntity<Cliente>(confirmacionCliente, HttpStatusCode.valueOf(200));
		}catch (Exception e) {
			return new ResponseEntity<String>("Error al dar de alta el cliente", HttpStatusCode.valueOf(500));
		}
	}
	
	@GetMapping("cliente-dni/{documento}")
	public ResponseEntity<?>buscarClientePorDocumento(@PathVariable String documento) {
		Optional<Cliente> cliente = clienteService.buscarPorDocumento(documento);
		if (cliente.isPresent()) {
			return ResponseEntity.ok(cliente.get());		// Equivale a: new ResponseEntity<>(cliente.get(), HttpStatus.OK)
		} else {
			return new ResponseEntity<>("Cliente no encontrado con documento: " + documento, HttpStatusCode.valueOf(404));
			//Ejemplo con métodos de opcional:
			//return ResponseEntity.notFound().build();		//.notFound() prepara la respuesta 404, build() la crea.
		}
	}
	
	//TODO: generar eliminarCliente mediante dni
	
	
	
	
	
	
	
}
