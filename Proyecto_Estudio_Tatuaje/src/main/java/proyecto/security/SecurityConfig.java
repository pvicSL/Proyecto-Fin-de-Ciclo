package proyecto.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // 1. AUTH endpoints (siempre públicos)
                .requestMatchers("/auth/login").permitAll()
                
                // 2. RECURSOS estáticos (siempre públicos)
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/presupuesto_nuevo").permitAll()
                
                // 3. CITAS - Configuración flexible
                // FASE TESTING (actual): Todo público
                .requestMatchers("/api/citas/**").permitAll()
                
                // FASE PRODUCCIÓN (para el futuro):
                // .requestMatchers("/api/citas/crear-cita", "/api/citas/disponibilidad/**").permitAll()
                // .requestMatchers("/api/citas/buscar", "/api/citas/calcular-duracion").permitAll()
                // .requestMatchers("/api/citas/modificar-conreferencia", "/api/citas/cancelar-conreferencia").permitAll()
                // .requestMatchers("/api/citas/asignar-trabajador/**", "/api/citas/desasignar-trabajador/**").permitAll()
                // .requestMatchers("/api/citas/calculo-previo", "/api/citas/huecos-disponibles").permitAll()
                // .requestMatchers("/api/citas/**").authenticated()  // Resto requiere login
                
                // 4. OTROS endpoints API (por configurar después)
                .requestMatchers("/api/**").permitAll()  // Por ahora público
                
                // 5. RUTAS por rol (para el futuro)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/trabajador/**").hasAnyRole("ADMIN", "TRABAJADOR")
                
                // 6. DEFAULT: Requerir autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
