package proyecto.modelo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.Estado;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer>{

	@Query("SELECT c FROM Cita c " +
		       "JOIN Presupuesto p ON p.idServicio = c.idCita " +
		       "WHERE p.estado = :estadoPresupuesto")
		List<CitaDTO> findByEstadoPresupuesto(@Param("estadoPresupuesto") Estado estadoPresupuesto);

	Optional<Presupuesto> findByIdServicio(int idServicio);
	
	List<Presupuesto> findByEstadoAndFechaBefore(Estado estado, LocalDateTime fecha);
	void deleteByEstadoAndFechaBefore(Estado estado, LocalDateTime fecha);
}
