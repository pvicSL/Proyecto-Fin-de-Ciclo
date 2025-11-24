package proyecto.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.repository.*;




@Service
public class UserDataServiceImpl implements UserDetailsService {

	@Autowired
    private TrabajadorRepository trabajadorRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
        Trabajador trabajador = trabajadorRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        
     // 2. Convertir a UserDetails (formato que Spring Security entiende)
        return User.builder()
            .username(trabajador.getEmail())
            .password(trabajador.getContrasenia())
            .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + trabajador.getRol().toString())))
            .build();
		
	}
	
}
