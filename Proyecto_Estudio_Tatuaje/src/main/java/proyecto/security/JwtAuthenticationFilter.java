package proyecto.security;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;           
import jakarta.servlet.ServletException;     

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
		@Autowired
	    private JwtService jwtService;
	    
	    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                FilterChain filterChain) throws ServletException, IOException {
	        
	        // 1. Extraer token del header Authorization
	        String authHeader = request.getHeader("Authorization");
	        String token = null;
	        String email = null;
	        
	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            token = authHeader.substring(7); // Quitar "Bearer "
	            
	            try {
	                // 2. Extraer email del token
	                email = jwtService.extraerEmail(token);
	            } catch (Exception e) {
	                // Token inválido - continuar sin autenticación
	            }
	        }
	        
	        // 3. Si hay email y no hay autenticación previa
	        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	            
	            // 4. Validar token
	            if (jwtService.esTokenValido(token)) {
	                
	            	// 5. Extraer rol Y idTrabajador del token
	            	String role = jwtService.extraerRole(token);
	            	Integer idTrabajador = jwtService.extraerIdTrabajador(token);  // ← NUEVO

	            	// 6. Crear autenticación para Spring Security
	            	UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
	            	    email,
	            	    null,
	            	    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
	            	);
	            	//Añadir idTrabajador a los detalles
	            	Map<String, Object> detallesExtra = new HashMap<>();
	            	detallesExtra.put("idTrabajador", idTrabajador);
	            	detallesExtra.put("rol", role);

	            	authToken.setDetails(detallesExtra);

	            	// 7. Establecer autenticación en el contexto
	            	SecurityContextHolder.getContext().setAuthentication(authToken);
	            }
	        }
	        
	        // 8. Continuar con la cadena de filtros
	        filterChain.doFilter(request, response);
	    }
}
