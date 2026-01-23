package proyecto.security;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    
    @Autowired
    private JwtService jwtService;
    
    /**
     * Obtiene los datos del usuario autenticado desde el JWT
     * @return Map con: "email", "rol", "idTrabajador", "esAdmin"
     */
    public Map<String, Object> obtenerDatosUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> datosUsuario = new HashMap<>();
        
        if (auth != null && auth.isAuthenticated()) {
            // 1. Email del usuario
            String email = auth.getName();
            datosUsuario.put("email", email);
            
            // 2. Verificar si es ADMIN
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            boolean esAdmin = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            datosUsuario.put("esAdmin", esAdmin);
            
            // 3. Obtener datos extra del JWT (rol e idTrabajador)
            if (auth.getDetails() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> detalles = (Map<String, Object>) auth.getDetails();
                
                // Rol desde los detalles
                String rol = (String) detalles.get("rol");
                datosUsuario.put("rol", rol != null ? rol : (esAdmin ? "ADMIN" : "TRABAJADOR"));
                
                // IdTrabajador desde los detalles
                Integer idTrabajador = (Integer) detalles.get("idTrabajador");
                if (idTrabajador != null) {
                    datosUsuario.put("idTrabajador", idTrabajador);
                }
            }
        }
        
        return datosUsuario;
    }
    
    /**
     * Verifica si el usuario autenticado es ADMIN
     */
    public boolean esAdmin() {
        return (boolean) obtenerDatosUsuarioAutenticado().getOrDefault("esAdmin", false);
    }
    
    /**
     * Obtiene el idTrabajador del usuario autenticado (solo para trabajadores)
     */
    public Integer obtenerIdTrabajador() {
        Map<String, Object> datos = obtenerDatosUsuarioAutenticado();
        return (Integer) datos.get("idTrabajador");
    }
    
    /**
     * Obtiene el rol del usuario autenticado
     */
    public String obtenerRol() {
        return (String) obtenerDatosUsuarioAutenticado().get("rol");
    }
}