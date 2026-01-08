package proyecto.modelo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.Estado;

public interface PresupuestoRepository extends JpaRepository<Presupuesto, Integer>{
	
	// Método derivado para buscar por el campo idServicio
    Optional<Presupuesto> findByIdServicio(int idServicio);
    List<Presupuesto> findByEstado(Estado estado);
    


}