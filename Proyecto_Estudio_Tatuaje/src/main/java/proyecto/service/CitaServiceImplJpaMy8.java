package proyecto.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Cliente;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.ClienteRepository;

@Service
public class CitaServiceImplJpaMy8 implements CitaService{
	
	@Autowired
	private CitaRepository citaRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;

	@Override
	public List<Cita> leerTodos() {
		return citaRepository.findAll();
	}

	@Override
	public Cita buscarUnaCita(int idCita) {
		return citaRepository.findById(idCita).orElse(null);
	}

	@Override
	public Cita crearCita(Cita cita) {
	    
	    // Si viene un idCliente en el JSON, buscar y asignar el cliente
	    if (cita.getCliente() != null && cita.getCliente().getIdCliente() != 0) {
	        int idCliente = cita.getCliente().getIdCliente();
	        Cliente clienteCompleto = clienteRepository.findById(idCliente).orElse(null);
	        cita.setCliente(clienteCompleto);
	    }
	    
	    return citaRepository.save(cita);
	}

	@Override
	public int eliminarCita(int idCita) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Cita actualizarCita(Cita cita) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Cita> buscarPorCliente(String email) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	
	// CRUD básico con DTOs
    @Override
    public List<CitaDTO> listarCitasDTO() {
        List<Cita> citas = citaRepository.findAll();
        return citas.stream()
                .map(cita -> new CitaDTO(cita))  // ← Constructor DTO hace la conversión
                .collect(Collectors.toList());
    }

    @Override
    public CitaDTO obtenerCitaDTOPorId(int idCita) {
        Optional<Cita> citaOpt = citaRepository.findById(idCita);
        
        if (citaOpt.isPresent()) {
            return new CitaDTO(citaOpt.get());  // ← Convertir entidad → DTO
        } else {
            return null;  // O lanzar excepción personalizada
        }
    }

}
