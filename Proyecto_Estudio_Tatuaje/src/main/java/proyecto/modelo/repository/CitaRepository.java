package proyecto.modelo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.Estatus;


public interface CitaRepository extends JpaRepository<Cita, Integer>{

	List<Cita> findByFecha(LocalDate fechaRevision);
	
	// Busca citas entre el inicio y el fin del rango con status CONFIRMADA
    List<Cita> findByEstatusAndFechaBetween(Estatus status, LocalDate inicio, LocalDate fin);
    
    List<CitaDTO> findByEstatus(Estatus estatus);

}
