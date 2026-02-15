package proyecto.modelo.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.modelo.dto.CitaCompletaDTO;
import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.enums.EstadoFactura;
import proyecto.modelo.enums.Estatus;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

	List<Cita> findByFecha(LocalDate fechaRevision);
	
	// Busca citas entre el inicio y el fin del rango con status CONFIRMADA
    List<Cita> findByEstatusAndFechaBetween(Estatus status, LocalDate inicio, LocalDate fin);
    
    List<CitaDTO> findByEstatus(Estatus estatus);

    void deleteByIdCitaIn(List<Integer> ids);
    
	Optional<Cita> findByReferenciaAndClienteEmail(String referencia, String email);
	
	boolean existsByReferencia(String referencia);
	
	@Query("SELECT new proyecto.modelo.dto.CitaCompletaDTO(" +
		       "c.idCita, " +
		       "CAST(c.tipo AS string), CAST(c.zona AS string), CAST(c.tamanio AS string), " +
		       "CAST(c.detalle AS string), CAST(c.coloracion AS string), CAST(c.estilo AS string), " +
		       "c.fecha, c.hora, c.comentarios, c.imagenRef1, c.imagenRef2, c.imagenRef3, " +
		       "cl.nombre, cl.apellido1, cl.apellido2, cl.email, cl.telefono, cl.documentoIdentificacion, " +
		       "COALESCE(p.precioSinIva, 0), COALESCE(p.iva, 0), COALESCE(p.precioFinal, 0), " +
		       "COALESCE(p.fecha, CURRENT_TIMESTAMP), COALESCE(CAST(p.estado AS string), 'PENDIENTE'), " +
		       "COALESCE(p.vigente, false), COALESCE(p.comentarios, '')) " +
		       "FROM Cita c " +
		       "JOIN c.cliente cl " +
		       "LEFT JOIN Presupuesto p ON p.idServicio = c.idCita " +
		       "WHERE c.idCita = :id")
		CitaCompletaDTO findCitaCompletaById(@Param("id") int id);

	@Query("SELECT c FROM Cita c " +
		       "JOIN c.cliente cl " +
		       "JOIN Presupuesto p ON p.idServicio = c.idCita " +
		       "WHERE p.estado = :estadoPresupuesto")
		List<CitaDTO> obtenerPorEstadoPresupuesto(@Param("estadoPresupuesto") Estado estadoPresupuesto);

	List<Cita> findByTrabajadorIdTrabajadorAndFecha(int idTrabajador, LocalDate fechaRevision);

	// Buscar citas asignadas a un trabajador específico
	List<Cita> findByTrabajador_IdTrabajador(Integer idTrabajador);
	
    // 1. Busca citas pendientes cuya fecha límite ya ha pasado (para borrar)
    List<Cita> findByEstatusAndFechaLimitePagoBefore(Estatus estatus, LocalDateTime fechaLimite);
    
    // 2. Busca por referencia (para confirmar el pago)
    Optional<Cita> findByReferencia(String referencia);

	
    
    @Query("SELECT new proyecto.modelo.dto.CitaCompletaDTO(" +
    	       "c.idCita, " +
    	       "CAST(c.tipo AS string), CAST(c.zona AS string), CAST(c.tamanio AS string), " +
    	       "CAST(c.detalle AS string), CAST(c.coloracion AS string), CAST(c.estilo AS string), " +
    	       "c.fecha, c.hora, c.comentarios, c.imagenRef1, c.imagenRef2, c.imagenRef3, " +
    	       "cl.nombre, cl.apellido1, cl.apellido2, cl.email, cl.telefono, cl.documentoIdentificacion, " +
    	       "COALESCE(p.precioSinIva, 0), COALESCE(p.iva, 0), COALESCE(p.precioFinal, 0), " +
    	       "COALESCE(p.fecha, CURRENT_TIMESTAMP), COALESCE(CAST(p.estado AS string), 'PENDIENTE'), " +
    	       "COALESCE(p.vigente, false), COALESCE(p.comentarios, '')) " +
    	       "FROM Cita c " +
    	       "JOIN c.cliente cl " +
    	       "LEFT JOIN Presupuesto p ON p.idServicio = c.idCita " +
    	       "WHERE c.estadoFactura = :estado")
    	List<CitaCompletaDTO> findByEstadoFactura(@Param("estado") EstadoFactura estado);



}
