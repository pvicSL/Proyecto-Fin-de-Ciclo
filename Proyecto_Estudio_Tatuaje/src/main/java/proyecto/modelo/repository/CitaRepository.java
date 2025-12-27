package proyecto.modelo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.entities.Cita;


public interface CitaRepository extends JpaRepository<Cita, Integer>{

	List<Cita> findByFecha(LocalDate fechaRevision);

}
