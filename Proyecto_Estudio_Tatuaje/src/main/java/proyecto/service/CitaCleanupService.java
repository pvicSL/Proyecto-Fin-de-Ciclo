package proyecto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.enums.Estatus;
import proyecto.modelo.repository.CitaRepository;

@Service
public class CitaCleanupService {

    @Autowired
    private CitaRepository citaRepository;

    // Se ejecuta automáticamente cada 1 hora (3600000 ms)
    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void liberarCitasCaducadas() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Cita> caducadas = citaRepository.findByEstatusAndFechaLimitePagoBefore(Estatus.PENDIENTE, ahora);

        if (!caducadas.isEmpty()) {
            System.out.println(">>> [CLEANUP] Borrando " + caducadas.size() + " citas caducadas por impago.");
            citaRepository.deleteAll(caducadas);
        }
    }
}

