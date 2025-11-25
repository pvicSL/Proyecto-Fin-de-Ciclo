package proyecto.service;

import java.util.List;

import proyecto.modelo.entities.Servicio;


public interface ServicioService {

	List<Servicio>leerTodos();
	Servicio buscarUnServicio(int idServicio);
	Servicio altaServicio(Servicio servicio);
	int eliminarServicio(int idServicio);
	Servicio actualizarServicio(Servicio servicio);
	
}
