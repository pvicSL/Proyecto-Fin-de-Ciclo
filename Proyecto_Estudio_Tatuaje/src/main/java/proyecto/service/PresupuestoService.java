package proyecto.service;


import java.util.List;


import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.entities.Servicio;


/*Realiza los cálculos*/

public interface PresupuestoService {

	List<Presupuesto>leerTodos();
	Presupuesto buscarUnPresupuesto(int idPresupuesto);
	Presupuesto altaPresupuesto(Presupuesto presupuesto);
	int eliminarPresupuesto(int idPresupuesto);
	Presupuesto actualizarPresupuesto(Presupuesto presupuesto);
	
	
	Presupuesto calcularPresupuesto (Servicio servicio);
	Presupuesto calcularPresupuestoPorId(int idServicio);
	
}
