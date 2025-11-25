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
	public Servicio buscarUnServicio(int idServicio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Servicio altaServicio(Servicio servicio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int eliminarServicio(int idServicio) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Servicio actualizarServicio(Servicio servicio) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
