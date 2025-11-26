package proyecto.service;

import java.util.List;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trabajador buscarUnTrabajador(int idTrabajador) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Trabajador altaTrabajador(Trabajador trabajador) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int eliminarTrabajador(int idTrabajador) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Trabajador actualizarTrabajador(Trabajador trabajador) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
