package proyecto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.stream.Collectors;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.PresupuestoRepository;

@Service
@Transactional
public class LimpiezaServiceImplMy8 implements LimpiezaService {
    
    private static final Logger log = LoggerFactory.getLogger(LimpiezaServiceImplMy8.class);
    
    @Autowired
    private PresupuestoRepository presupuestoRepository;
    
    @Autowired
    private CitaRepository citaRepository;
    
    @Override
    @Scheduled(cron = "0 0 13 * * *") // Todos los días a las 13:00
    public void limpiarDatosAntiguos() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusMonths(3);

        try {
            log.info("Iniciando limpieza automática de datos anteriores a: {}", fechaLimite);

            // 1. Buscar presupuestos finalizados antiguos
            List<Presupuesto> presupuestosAEliminar = presupuestoRepository
                .findByEstadoAndFechaBefore(Estado.FINALIZADO, fechaLimite);

            if (presupuestosAEliminar.isEmpty()) {
                log.info("No se encontraron presupuestos finalizados para eliminar");
                return;
            }

            // 2. Obtener IDs de citas asociadas
            List<Integer> citasIds = presupuestosAEliminar.stream()
                .map(Presupuesto::getIdServicio)
                .collect(Collectors.toList());

            log.info("Se procederá a eliminar {} presupuestos y {} citas",
                presupuestosAEliminar.size(), citasIds.size());

            // 3. Eliminar presupuestos
            presupuestoRepository.deleteByEstadoAndFechaBefore(Estado.FINALIZADO, fechaLimite);
            log.info("Presupuestos eliminados correctamente");

            // 4. Eliminar citas
            citaRepository.deleteByIdCitaIn(citasIds);
            log.info("Citas eliminadas correctamente");

            log.info("Limpieza automática completada exitosamente. Eliminados {} registros",
                presupuestosAEliminar.size() + citasIds.size());

        } catch (Exception e) {
            log.error("Error durante la limpieza automática: {}", e.getMessage(), e);
            throw e; // Re-lanzar para activar rollback
        }
    }

	@Override
	public void ejecutarLimpiezaManual() {
		log.info("Ejecutando limpieza manual solicitada por usuario");
		limpiarDatosAntiguos();
		
	}
}