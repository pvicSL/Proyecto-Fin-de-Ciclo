package proyecto.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.TrabajadorDTO;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.enums.Rol;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.TrabajadorRepository;

@Service
public class TrabajadorServiceImplJpaMy8 implements TrabajadorService {

	@Autowired
	private TrabajadorRepository trabajadorRepository;
	
	@Autowired
	private CitaRepository citaRepository;

	@Override
	public List<Trabajador> leerTodos() {
		return trabajadorRepository.findAll();
	}

	@Override
	public Trabajador buscarUnTrabajador(int idTrabajador) {
		return trabajadorRepository.findById(idTrabajador).orElse(null);
	}

	@Override
	public Trabajador altaTrabajador(Trabajador trabajador) {
		return trabajadorRepository.save(trabajador);
	}

	@Override
	public List<CitaDTO> obtenerCitasDelTrabajador(int trabajadorId) {
		// 1. Verificar que el trabajador existe
		Optional<Trabajador> trabajadorOpt = trabajadorRepository.findById(trabajadorId);
		if (!trabajadorOpt.isPresent()) {
			return new ArrayList<>(); // Lista vacía si el trabajador no existe
		}
		
		Trabajador trabajador = trabajadorOpt.get();
		
		// 2. Obtener las citas y convertirlas a DTO
		return trabajador.getCitas().stream()
				.map(cita -> new CitaDTO(cita))
				.collect(Collectors.toList());
	}

	@Override
	public int eliminarTrabajador(int idTrabajador) {
		// 1. Buscar el trabajador
		Optional<Trabajador> trabajadorOpt = trabajadorRepository.findById(idTrabajador);
		if (!trabajadorOpt.isPresent()) {
			return -1; // Código de error: trabajador no existe
		}
		
		Trabajador trabajador = trabajadorOpt.get();
		
		// 2. Verificar que no tenga citas asignadas
		int numeroCitas = trabajador.getCitas().size();
		if (numeroCitas > 0) {
			return numeroCitas; // Retorna el número de citas pendientes
		}
		
		// 3. Si llega aquí, puede eliminarse sin problemas
		trabajadorRepository.deleteById(idTrabajador);
		return 0; // Código de éxito
	}

	@Override
	public Trabajador actualizarTrabajador(Trabajador trabajador) {
		return trabajadorRepository.save(trabajador);
	}

	@Override
	public Optional<Trabajador> buscarPorDocumento(String documento) {
		return trabajadorRepository.findByDniIgnoreCase(documento);
	}

	@Override
	public List<TrabajadorDTO> obtenerTrabajadoresPorRol(Rol rol) {
	    List<Trabajador> trabajadores = trabajadorRepository.findByRol(rol);
	    return trabajadores.stream()
	            .map(this::convertirADTO)
	            .collect(Collectors.toList());
	}
    
    private TrabajadorDTO convertirADTO(Trabajador trabajador) {
        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setIdTrabajador(trabajador.getIdTrabajador());
        dto.setNombre(trabajador.getNombre());
        dto.setApellido1(trabajador.getApellido1());
        dto.setApellido2(trabajador.getApellido2());
        dto.setEmail(trabajador.getEmail());
        dto.setTelefono(trabajador.getTelefono());
        return dto;
    }
}
	

