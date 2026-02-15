package proyecto.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.transaction.Transactional;
import proyecto.modelo.entities.PasswordResetToken;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.repository.PasswordResetTokenRepository;

public class PasswordResetTokenServiceImpl implements PasswordResetTokenService{

	
	 @Autowired
	 private PasswordResetTokenRepository tokenRepository;
	 
	 
	@Override
	@Transactional
	public PasswordResetToken crearToken(Trabajador trabajador) {
		// 1. Eliminar tokens previos del trabajador
        eliminarTokensDelTrabajador(trabajador);
        
        // 2. Generar nuevo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, trabajador);
        
        // 3. Guardar y retornar
        return tokenRepository.save(resetToken);
	}

	@Override
	public PasswordResetToken buscarPorToken(String token) {
		return tokenRepository.findByToken(token).orElse(null);
	}

	@Override
	@Transactional
	public void marcarComoUtilizado(String token) {
		 PasswordResetToken resetToken = buscarPorToken(token);
	        if (resetToken != null) {
	            resetToken.setUtilizado(true);
	            tokenRepository.save(resetToken);
	        }
		
	}

	@Override
	@Transactional
	public void eliminarTokensExpirados() {
		tokenRepository.deleteByFechaExpiracionBeforeAndUtilizadoTrue(LocalDateTime.now());
		
	}

	@Override
	@Transactional
	public void eliminarTokensDelTrabajador(Trabajador trabajador) {
		tokenRepository.deleteByTrabajador(trabajador);
		
	}

	@Override
	public boolean esTokenValidoYNoExpirado(String token) {
		PasswordResetToken resetToken = buscarPorToken(token);
        return resetToken != null && !resetToken.isUtilizado() && !resetToken.isExpired();
	}

	@Override
	public long contarTokensCreados(LocalDateTime fechaDesde) {
		return tokenRepository.countByFechaCreacionAfter(fechaDesde);
	}

}
