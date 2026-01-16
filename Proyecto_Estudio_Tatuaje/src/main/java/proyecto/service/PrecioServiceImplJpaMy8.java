package proyecto.service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.PrecioRepository;
import proyecto.modelo.repository.PresupuestoRepository;

@Service
public class PrecioServiceImplJpaMy8 implements PrecioService {

	
	private static final BigDecimal IVA_PORCENTAJE = new BigDecimal("0.21"); // 21%
	
	@Autowired
	private PrecioRepository precioRepository;
	
	@Autowired
	private CitaRepository citaRepository;
	
	@Autowired
	private PresupuestoService presupuestoService;
	
	
	@Override
	public List<Precio> leerTodos() {
		return precioRepository.findAll();
	}

	@Override
	public Precio buscarUnPrecio(int idPrecio) {
		return precioRepository.findById(idPrecio).orElse(null);
	}

	@Override
	public Precio altaPrecio(Precio precio) {
	    return precioRepository.save(precio);
	}

	@Override
	public int eliminarPrecio(int idPrecio) {
	    if (precioRepository.existsById(idPrecio)) {
	        precioRepository.deleteById(idPrecio);
	        return 1; // Éxito
	    }
	    return 0; // No se encontró el precio
	}

	@Override
	public Precio actualizarPrecio(Precio precio) {
		return precioRepository.save(precio);
	}

	@Override
	public Precio encontrarPorCategoriaValor(CategoriaEnum categoria, String valor) {
	    return precioRepository.findByCategoriaAndValor(categoria, valor).orElse(null);
	}

	

	

}
