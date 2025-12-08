package proyecto.modelo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.entities.Trabajador;

public interface TrabajadorRepository extends JpaRepository<Trabajador, Integer>{

	/*Optional: Puede contener un valor o estar vacío*/
	Optional<Trabajador> findByEmail(String email);
	
	Optional<Trabajador> findByDniIgnoreCase(String documento);
}
