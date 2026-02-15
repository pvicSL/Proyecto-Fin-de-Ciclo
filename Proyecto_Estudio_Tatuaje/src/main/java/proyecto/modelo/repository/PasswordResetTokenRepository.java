package proyecto.modelo.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.modelo.entities.PasswordResetToken;
import proyecto.modelo.entities.Trabajador;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer>{
	Optional<PasswordResetToken> findByToken(String token);
    
    Optional<PasswordResetToken> findByTrabajadorAndUtilizadoFalse(Trabajador trabajador);
    
    List<PasswordResetToken> findByFechaExpiracionBeforeAndUtilizadoFalse(LocalDateTime fecha);
    
    void deleteByFechaExpiracionBeforeAndUtilizadoTrue(LocalDateTime fecha);
    
    void deleteByTrabajador(Trabajador trabajador);
    
    @Query("SELECT COUNT(p) FROM PasswordResetToken p WHERE p.fechaCreacion >= :fechaDesde")
    long countByFechaCreacionAfter(@Param("fechaDesde") LocalDateTime fechaDesde);
}
