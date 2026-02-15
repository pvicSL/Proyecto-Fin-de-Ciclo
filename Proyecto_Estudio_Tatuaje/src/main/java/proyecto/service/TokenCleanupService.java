package proyecto.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import jakarta.transaction.Transactional;

public class TokenCleanupService {

	
	@Autowired
    private PasswordResetTokenService tokenService;
	
	private static final Logger log = LoggerFactory.getLogger(LimpiezaServiceImplMy8.class);
	
	/**
     * Ejecuta cada 30 minutos para limpiar tokens expirados
     */
	
	@Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void limpiarTokensExpirados() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            tokenService.eliminarTokensExpirados();
            
            System.out.println("🧹 [" + timestamp + "] Limpieza automática de tokens expirados completada");
            
        } catch (Exception e) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.err.println("❌ [" + timestamp + "] Error en limpieza automática: " + e.getMessage());
        }
    }
	
	/**
     * Limpieza diaria más profunda - Todos los días a las 2:00 AM
     * Elimina tokens utilizados antiguos (más de 7 días)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void limpiezaProfundaDiaria() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // Eliminar tokens expirados
            tokenService.eliminarTokensExpirados();
            
            System.out.println("🧹 [" + timestamp + "] Limpieza profunda diaria completada");
            
        } catch (Exception e) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            System.err.println("❌ [" + timestamp + "] Error en limpieza profunda: " + e.getMessage());
        }
    }
    
    /**
     * OPCIONAL: Estadísticas semanales - Domingos a las 10:00 AM
     */
    @Scheduled(cron = "0 0 10 * * SUN")
    public void estadisticasSemanales() {
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            
            // ✅ IMPLEMENTADO: Conteo de tokens creados esta semana
            long tokensCreados = tokenService.contarTokensCreados(LocalDateTime.now().minusDays(7));
            
            log.info("📊 [{}] Estadísticas semanales - Tokens de recuperación creados: {}", 
                     timestamp, tokensCreados);
            
            // Opcional: más estadísticas
            if (tokensCreados > 10) {
                log.warn("⚠️ Muchos intentos de recuperación esta semana: {}", tokensCreados);
            }
            
        } catch (Exception e) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            log.error("❌ [{}] Error en estadísticas: {}", timestamp, e.getMessage());
        }
    }
}
