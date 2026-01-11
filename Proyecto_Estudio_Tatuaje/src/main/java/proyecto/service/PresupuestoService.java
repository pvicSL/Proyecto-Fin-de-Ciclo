package proyecto.service;


import java.util.List;

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
	
	
}
