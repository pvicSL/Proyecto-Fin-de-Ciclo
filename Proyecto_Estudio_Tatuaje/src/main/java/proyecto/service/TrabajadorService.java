package proyecto.service;

import java.util.List;

import proyecto.modelo.entities.Trabajador;

public interface TrabajadorService {

	List<Trabajador>leerTodos();
	Trabajador buscarUnTrabajador(int idTrabajador);
	Trabajador altaTrabajador(Trabajador trabajador);
	int eliminarTrabajador(int idTrabajador);
	Trabajador actualizarTrabajador(Trabajador trabajador);
	
}
