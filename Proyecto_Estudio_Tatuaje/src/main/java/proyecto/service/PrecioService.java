package proyecto.service;


import java.util.List;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.enums.CategoriaEnum;


public interface PrecioService {

	List<Precio>leerTodos();
	Precio buscarUnPrecio(int idPrecio);
	Precio altaPrecio(Precio precio);
	int eliminarPrecio(int idPrecio);
	Precio actualizarPrecio(Precio precio);
	
	Precio encontrarPorCategoriaValor (CategoriaEnum categoria, String valor);
}
