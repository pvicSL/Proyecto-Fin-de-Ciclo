package proyecto.service;


import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.repository.PrecioRepository;

@Service
public class PrecioServiceImplJpaMy8 implements PrecioService {

	@Autowired
	private PrecioRepository precioRepository;
	
	
	@Override
	public List<Precio> leerTodos() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Precio buscarUnPrecio(int idPrecio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Precio altaPrecio(Precio precio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int eliminarPrecio(int idPrecio) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Precio actualizarPrecio(Precio precio) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Precio encontrarPorCategoriaValor(CategoriaEnum categoria, String valor) {
	    return precioRepository.findByCategoriaAndValor(categoria, valor).orElse(null);
	}

	

}
