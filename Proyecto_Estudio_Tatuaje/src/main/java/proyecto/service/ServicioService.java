package proyecto.service;

import java.util.List;

import proyecto.modelo.entities.Servicio;


public interface ServicioService {

	List<Servicio>leerTodos();
	Servicio buscarUnTrabajador(int idServicio);
	Servicio altaTrabajador(Servicio servicio);
	int eliminarTrabajador(int idServicio);
	Servicio actualizarTrabajador(Servicio servicio);
	
}
