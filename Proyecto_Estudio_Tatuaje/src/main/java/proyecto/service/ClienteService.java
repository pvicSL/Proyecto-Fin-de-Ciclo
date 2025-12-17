package proyecto.service;

import java.util.List;
import java.util.Optional;

import proyecto.modelo.entities.Cliente;

public interface ClienteService {
	
	List<Cliente>leerTodos();
	Cliente buscarUnCliente(int idCliente);
	Cliente altaCliente(Cliente cliente);
	int eliminarCliente(int idCliente);
	Cliente actualizarCliente(Cliente cliente);
	Optional<Cliente> buscarPorDocumento(String documento);
	Optional<Cliente> findByEmail(String email);
	
	
	
}
