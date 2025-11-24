package proyecto.service;

import java.util.List;

import proyecto.modelo.entities.Cliente;

public interface ClienteService {
	
	List<Cliente>leerTodos();
	Cliente buscarUnCliente(int idCliente);
	Cliente altaCliente(Cliente cliente);
	int eliminarCliente(int idCliente);
	Cliente actualizarCliente(Cliente cliente);
	
	
}
