package proyecto.service;

import java.util.List;
import java.util.Optional;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.TrabajadorDTO;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.enums.Rol;

public interface TrabajadorService {

	List<Trabajador>leerTodos();
	Trabajador buscarUnTrabajador(int idTrabajador);
	Trabajador altaTrabajador(Trabajador trabajador);
	int eliminarTrabajador(int idTrabajador);
	Trabajador actualizarTrabajador(Trabajador trabajador);
	Optional<Trabajador>buscarPorDocumento(String documento);
	List<CitaDTO> obtenerCitasDelTrabajador(int trabajadorId);
	List<TrabajadorDTO> obtenerTrabajadoresPorRol(Rol rol);
	Optional<Trabajador> findByEmail(String email);
	
}
