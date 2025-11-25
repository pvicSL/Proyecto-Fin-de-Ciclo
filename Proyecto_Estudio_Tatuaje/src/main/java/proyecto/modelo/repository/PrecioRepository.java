package proyecto.modelo.repository;



import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.enums.CategoriaEnum;



public interface PrecioRepository extends JpaRepository<Precio, Integer>{

	Optional<Precio> findByCategoriaAndValor(CategoriaEnum categoria, String valor);

	
}
