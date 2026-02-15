package proyecto.service;

import proyecto.modelo.entities.PasswordResetToken;
import proyecto.modelo.entities.Trabajador;

public interface PasswordResetTokenService {
	PasswordResetToken crearToken(Trabajador trabajador);
    
    PasswordResetToken buscarPorToken(String token);
    
    void marcarComoUtilizado(String token);
    
    void eliminarTokensExpirados();
    
    void eliminarTokensDelTrabajador(Trabajador trabajador);
    
    boolean esTokenValidoYNoExpirado(String token);
}
