package proyecto.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import proyecto.modelo.entities.Cliente;
import proyecto.modelo.repository.ClienteRepository;

public class ClienteServiceImplJpaMy8 implements ClienteService {

	@Autowired
	ClienteRepository clienteRepository;

	@Override
	public List<Cliente> leerTodos() {
		return clienteRepository.findAll();
	}

	@Override
	public Cliente buscarUnCliente(int idCliente) {
		return clienteRepository.findById(idCliente).orElse(null);
	}

	@Override
	public Cliente altaCliente(Cliente cliente) {
		return clienteRepository.save(cliente);
	}

	@Override
	public int eliminarCliente(int idCliente) {
		// TODO es necesario hacerlo??
		return 0;
	}

	@Override
	public Cliente actualizarCliente(Cliente cliente) {
		return clienteRepository.save(cliente);
	}
}
