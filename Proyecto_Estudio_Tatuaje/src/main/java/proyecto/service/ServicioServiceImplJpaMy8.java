package proyecto.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.entities.Servicio;
import proyecto.modelo.repository.ServicioRepository;

@Service
public class ServicioServiceImplJpaMy8 implements ServicioService {

	@Autowired
	ServicioRepository servicioRepository;

	@Override
	public List<Servicio> leerTodos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servicio buscarUnTrabajador(int idServicio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servicio altaTrabajador(Servicio servicio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int eliminarTrabajador(int idServicio) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Servicio actualizarTrabajador(Servicio servicio) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
