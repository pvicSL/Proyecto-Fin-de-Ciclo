package proyecto.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import proyecto.modelo.dto.CitaCompletaDTO;

import org.springframework.web.multipart.MultipartFile;


import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.CitaModificacionDTO;
import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.enums.Estatus;

public interface CitaService {

	List<Cita> leerTodos();

	Cita buscarUnaCita(int idCita);

	Cita crearCita(Cita cita, List<MultipartFile> ficheros);

	int eliminarCita(int idCita);

	Cita actualizarCita(Cita cita);

	Optional<Cita> buscarPorCliente(String email);

	List<CitaDTO> listarCitasDTO();

	CitaDTO obtenerCitaDTOPorId(int idCita);

	Integer calcularDuracion(Cita cita);

    List<Cita> findByFecha(LocalDate fecha);
    Map<String, List<String>> buscarHuecosDisponibles(int duracionMinutos);
    List<CitaDTO> obtenerPorRango(LocalDate fecha, String vista);
    List<CitaDTO> obtenerPorEstatus(Estatus estatus);

    CitaCompletaDTO obtenerCitaCompleta(int id);

	Optional<Cita> buscarPorReferenciaYEmail(String referencia, String email);
	CitaCompletaDTO actualizarCitaCompleta(int id, CitaCompletaDTO citaEditada);

	List<CitaDTO> obtenerPorEstadoPresupuesto(Estado generado);

	PreciosIndividualesDTO obtenerPreciosIndividualesPorCita(int idCita);
	
    // Devuelve true si se modificó, false si no encontró la cita
    boolean modificarFechaCita(CitaModificacionDTO datos);

    // Devuelve true si se borró, false si no encontró la cita
    boolean cancelarCitaPorReferencia(String referencia, String email);
    

    String asignarTrabajador(int citaId, int trabajadorId);
    String desasignarTrabajador(int citaId);

	void aceptarPresupuesto(Cita cita);

	void rechazarPresupuesto(Cita cita);

}
