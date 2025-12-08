package proyecto.security;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;		// ← Leer application.properties
import org.springframework.stereotype.Service;		// ← Es un servicio Spring

import io.jsonwebtoken.Claims;						// ← Datos dentro del token
import io.jsonwebtoken.Jwts;						// ← Construir y leer tokens
import io.jsonwebtoken.security.Keys;
import proyecto.modelo.entities.Trabajador;


/*
 * Cuando haces LOGIN:

	generateToken(trabajador) → Crea token con email, rol, expiración 8h

	En cada petición posterior:
		extractEmail(token) → ¿Quién es? → "juan@tatuajes.com"
	 	extractRole(token) → ¿Qué permisos? → "ADMIN"
	 	isTokenValid(token) → ¿Token válido? → true/false
	 	*/

@Service
public class JwtService {

	@Value("${jwt.secret.key}")		
    private String secretKeyString;		// ← Lee "MiClaveSecretaTatuajes..."
    
    @Value("${jwt.expiration.hours}")
    private int expirationHours;		// ← Lee "8"
    
    // Generar token cuando el login es exitoso
    public String generateToken(Trabajador trabajador) {
    	// 1. Crear un "mapa" con datos extra para incluir en el token
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", List.of("ROLE_" + trabajador.getRol().toString()));		// ← "ADMIN" o "TRABAJADOR"
        claims.put("nombre", trabajador.getNombre());			// ← "Juan"
        
        return Jwts.builder()
                .claims(claims)					// ← Añadir datos extra
                .subject(trabajador.getEmail()) // El email es el "subject"
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationHours * 60 * 60 * 1000)) // 8 horas
                .signWith(getSecretKey())		// ← Firmar con tu clave secreta
                .compact();						// ← Convertir a String
    }
    
    // Extraer email del token
    public String extraerEmail(String token) {
        return extractAllClaims(token).getSubject();
    }
    
    /*Recibe un token: "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1..."
	Extrae el "subject" (que pusimos como email en generateToken)
	Devuelve: "juan@tatuajes.com"
	*/
    
    // Extraer rol del token
    public String extraerRole(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }
    
    /*
     * Busca en el token el dato "rol" que guardamos en claims.put("rol", ...)
	Devuelve: "ADMIN" o "TRABAJADOR"
	.get("rol", String.class) = Dame el campo "rol" como String
     * */
    
    // Verificar si el token es válido y no ha expirado
    public boolean esTokenValido(String token) {
        try {
            return !isTokenExpired(token);		// ← ¿NO está expirado?
        } catch (Exception e) {
            return false;						// ← Si da error = inválido
        }
    }
    
    /*¿Cuándo un token es inválido?
	Expirado: Pasaron más de 8 horas
	Modificado: Alguien cambió el contenido
	Firma incorrecta: No coincide con tu clave secreta
	Formato corrupto: Token mal formado
*/
    
   
    
    // MÉTODOS PRIVADOS (auxiliares)
    
    private boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();		// ← Hora de expiración
        return expiration.before(new Date());							// ← ¿Expiró antes de AHORA?
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()					// ← Crear un "parser" (lector de tokens)
                .verifyWith(getSecretKey())		// ← Verificar firma con tu clave secreta
                .build()						// ← Construir el parser
                .parseSignedClaims(token)		// ← Leer y verificar el token
                .getPayload();  				// ← Extraer el contenido (Claims)
    }
    
    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }
    
    /*secretKeyString = "MiClaveSecretaTatuajes..."
	.getBytes() → Convierte texto a bytes [77, 105, 67, 108, ...]
	Keys.hmacShaKeyFor() → Crea una clave criptográfica segura
	¿Por qué necesita ser clave criptográfica?
	Los tokens JWT usan algoritmo HMAC-SHA256
	Necesita formato específico, no solo texto plano*/
}
