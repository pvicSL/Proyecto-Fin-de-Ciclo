package proyecto.modelo.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.entities.Cita;

public interface CitaRepository extends JpaRepository<Cita, Integer> {

	List<Cita> findByFecha(LocalDate fechaRevision);

	// Método: Busca por la columna 'localizador' Y por el campo 'email' de la
	// entidad 'cliente'
	Optional<Cita> findByReferenciaAndCliente_Email(String referencia, String email);

}
