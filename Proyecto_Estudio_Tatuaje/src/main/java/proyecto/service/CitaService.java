package proyecto.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import proyecto.modelo.dto.CitaAdminDTO;

import org.springframework.web.multipart.MultipartFile;


import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.CategoriaEnum;
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
    CitaAdminDTO obtenerDetalleCita(int idServicio);




	Optional<Cita> buscarPorReferenciaYEmail(String referencia, String email);
}
