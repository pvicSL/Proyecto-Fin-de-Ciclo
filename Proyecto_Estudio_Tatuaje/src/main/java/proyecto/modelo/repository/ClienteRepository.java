package proyecto.modelo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.entities.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

}
