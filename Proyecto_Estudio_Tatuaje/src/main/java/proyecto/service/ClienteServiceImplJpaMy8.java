package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.entities.Cliente;
import proyecto.modelo.repository.ClienteRepository;

@Service
public class ClienteServiceImplJpaMy8 implements ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

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

	@Override
	public Optional<Cliente> buscarPorDocumento(String documento) {
		return clienteRepository.findByDocumentoIdentificacionIgnoreCase(documento);
	}
}
