package proyecto.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Presupuesto;



/*Realiza los cálculos*/

public interface PresupuestoService {

	List<Presupuesto>leerTodos();
	Presupuesto buscarUnPresupuesto(int idPresupuesto);
	Presupuesto altaPresupuesto(Presupuesto presupuesto);
	int eliminarPresupuesto(int idPresupuesto);
	Presupuesto actualizarPresupuesto(Presupuesto presupuesto);
	
	
	Presupuesto calcularPresupuesto (Cita cita);
	Presupuesto calcularPresupuestoPorId(int idCita);
	Map<String, BigDecimal> obtenerPreciosIndividuales(Cita cita);
	PreciosIndividualesDTO obtenerPreciosCompletosConIva(int idCita);
	 BigDecimal[] calcularSoloValores(Cita cita);
	 Optional<Presupuesto> findByIdServicio(int idServicio);
	
}
