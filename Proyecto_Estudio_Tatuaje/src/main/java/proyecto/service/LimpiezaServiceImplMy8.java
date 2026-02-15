package proyecto.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Value("${tatusys.limpieza.datos.meses-antiguedad:3}")
    private int mesesAntiguedad;
    
    @Autowired
    private PresupuestoRepository presupuestoRepository;
    
    @Autowired
    private CitaRepository citaRepository;
    
    // ✅ Configuración desde properties
    @Override
    @Scheduled(cron = "${tatusys.limpieza.datos.cron:0 0 13 * * *}")
    public void limpiarDatosAntiguos() {
        LocalDateTime fechaLimite = LocalDateTime.now().minusMonths(mesesAntiguedad);
        
        try {
            log.info("🧹 Iniciando limpieza automática de datos anteriores a: {}", fechaLimite);
            
            List<Presupuesto> presupuestosAEliminar = presupuestoRepository
                .findByEstadoAndFechaBefore(Estado.FINALIZADO, fechaLimite);
            
            if (presupuestosAEliminar.isEmpty()) {
                log.info("✅ No se encontraron presupuestos finalizados para eliminar");
                return;
            }
            
            List<Integer> citasIds = presupuestosAEliminar.stream()
                .map(Presupuesto::getIdServicio)
                .collect(Collectors.toList());
            
            log.info("Se procederá a eliminar {} presupuestos y {} citas",
                presupuestosAEliminar.size(), citasIds.size());
            
            // Eliminar
            presupuestoRepository.deleteByEstadoAndFechaBefore(Estado.FINALIZADO, fechaLimite);
            citaRepository.deleteByIdCitaIn(citasIds);
            
            log.info("✅ Limpieza completada: {} presupuestos + {} citas eliminados", 
                     presupuestosAEliminar.size(), citasIds.size());
            
        } catch (Exception e) {
            log.error("❌ Error durante la limpieza automática", e);
            throw e;
        }
    }

    @Override
    public void ejecutarLimpiezaManual() {
        log.info("🔧 Ejecutando limpieza manual solicitada por usuario");
        limpiarDatosAntiguos();
    }
}
