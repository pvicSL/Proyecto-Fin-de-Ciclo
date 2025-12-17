package proyecto.service;

import java.util.List;
import java.util.Optional;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;

public interface CitaService {
	
	List<Cita>leerTodos();
	Cita buscarUnaCita(int idCita);
	Cita crearCita(Cita cita);
	int eliminarCita(int idCita);
	Cita actualizarCita(Cita cita);
	Optional<Cita>buscarPorCliente(String email);
	List<CitaDTO> listarCitasDTO();
	CitaDTO obtenerCitaDTOPorId(int idCita);
	Integer calcularDuracion(Cita cita);
}
