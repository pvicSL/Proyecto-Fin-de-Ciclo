package proyecto.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(java.util.List.of("http://localhost:4200", "http://localhost:4201"));
                corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
            .authorizeHttpRequests(authorize -> authorize
                // 1. AUTH endpoints (siempre públicos)
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/migrar-contraseñas").permitAll() //TODO:BORRAR UNA VEZ MIGRADAS LAS CONTRASEÑAS!!!!!!
                .requestMatchers("/api/citas/confirmar-pago/**").permitAll()

                // 2. RECURSOS estáticos (siempre públicos)
                .requestMatchers("/index.html").permitAll()
                .requestMatchers("/uploads/**").permitAll()
                .requestMatchers("/presupuesto_nuevo").permitAll()

                // 3. TRABAJADORES - Configuración por roles
                // 🔴 Solo ADMIN
                .requestMatchers(HttpMethod.POST, "/api/admin/trabajador-alta").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/trabajadores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/trabajadores/todos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/admin/rol/**").hasRole("ADMIN")

                // 🟡 ADMIN + TRABAJADOR (con restricciones a nivel de lógica)
                .requestMatchers(HttpMethod.GET, "/api/admin/trabajadores/*").hasAnyRole("ADMIN", "TRABAJADOR")
                .requestMatchers(HttpMethod.PUT, "/api/admin/trabajadores/*").hasAnyRole("ADMIN", "TRABAJADOR")
                .requestMatchers(HttpMethod.GET, "/api/admin/trabajadores/*/citas").hasAnyRole("ADMIN", "TRABAJADOR")

                // 🟠 ADMIN + TRABAJADOR (sin restricciones)
                .requestMatchers(HttpMethod.GET, "/api/admin/trabajador-dni/**").hasAnyRole("ADMIN", "TRABAJADOR")

                // 4. CITAS - Configuración flexible
                // FASE PRODUCCIÓN (actual):
                .requestMatchers("/api/citas/crear-cita", "/api/citas/disponibilidad/**").permitAll()
                .requestMatchers("/api/citas/buscar", "/api/citas/calcular-duracion").permitAll()
                .requestMatchers("/api/citas/modificar-conreferencia", "/api/citas/cancelar-conreferencia").permitAll()
                .requestMatchers("/api/citas/asignar-trabajador/**", "/api/citas/desasignar-trabajador/**").permitAll()
                .requestMatchers("/api/citas/calculo-previo", "/api/citas/huecos-disponibles").permitAll()
                .requestMatchers("/api/citas/**").authenticated()  // Resto requiere login

                // 5. OTROS endpoints API (por configurar después)
                .requestMatchers("/api/**").permitAll()  // Por ahora público

                // 6. RUTAS por rol
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/trabajador/**").hasAnyRole("ADMIN", "TRABAJADOR")

                // 7. DEFAULT: Requerir autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
