package proyecto.modelo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>{
	
	Optional<Cliente> findByDocumentoIdentificacionIgnoreCase(String documento);

	Optional<Cliente> findByEmail(String email);
	
}
