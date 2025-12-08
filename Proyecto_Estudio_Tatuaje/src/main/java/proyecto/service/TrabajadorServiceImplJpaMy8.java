package proyecto.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.repository.TrabajadorRepository;

@Service
public class TrabajadorServiceImplJpaMy8 implements TrabajadorService {

	@Autowired
	private TrabajadorRepository trabajadorRepository;

	@Override
	public List<Trabajador> leerTodos() {
		return trabajadorRepository.findAll();
	}

	@Override
	public Trabajador buscarUnTrabajador(int idTrabajador) {
		return trabajadorRepository.findById(idTrabajador).orElse(null);
	}

	@Override
	public Trabajador altaTrabajador(Trabajador trabajador) {
		return trabajadorRepository.save(trabajador);
	}

	@Override
	public int eliminarTrabajador(int idTrabajador) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Trabajador actualizarTrabajador(Trabajador trabajador) {
		return trabajadorRepository.save(trabajador);
	}

	@Override
	public Optional<Trabajador> buscarPorDocumento(String documento) {
		return trabajadorRepository.findByDniIgnoreCase(documento);
	}
	
}
