package proyecto.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.entities.Precio;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.entities.Servicio;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.repository.PrecioRepository;
import proyecto.modelo.repository.PresupuestoRepository;
import proyecto.modelo.repository.ServicioRepository;

@Service
public class PresupuestoServiceImplJpaMy8 implements PresupuestoService{

	
	private static final BigDecimal IVA_PORCENTAJE = new BigDecimal("0.21"); // 21%
	
	@Autowired
	private PresupuestoRepository presupuestoRepository;
	
	@Autowired
	private PrecioRepository precioRepository;
	
	@Autowired
	ServicioRepository servicioRepository;

	@Override
	public List<Presupuesto> leerTodos() {
		return presupuestoRepository.findAll();
	}

	@Override
	public Presupuesto buscarUnPresupuesto(int idPresupuesto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Presupuesto altaPresupuesto(Presupuesto presupuesto) {
		return presupuestoRepository.save(presupuesto);
	}

	@Override
	public int eliminarPresupuesto(int idPresupuesto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Presupuesto actualizarPresupuesto(Presupuesto presupuesto) {
		// TODO Auto-generated method stub
		return null;
	}

	
	

	@Override
	public Presupuesto calcularPresupuesto(Servicio servicio) {
		
		/*Extraer valores del servicio y almacenar en variables*/
		String tipoServicio = servicio.getTipo().toString();
		String zonaServicio = servicio.getZona().toString();
		String tamanioServicio = servicio.getTamanio().toString();
		String detalleServicio = servicio.getDetalle().toString();
		String coloracionServicio = servicio.getColoracion().toString();
		String estiloServicio = servicio.getEstilo().toString();
		
		/*Se optiene el objeto Precio completo de la BBDD*/
		Optional<Precio> precioTipo = precioRepository.findByCategoriaAndValor(CategoriaEnum.TIPO, tipoServicio);
		Optional<Precio> precioZona = precioRepository.findByCategoriaAndValor(CategoriaEnum.ZONA, zonaServicio);
		Optional<Precio> precioTamanio = precioRepository.findByCategoriaAndValor(CategoriaEnum.TAMANIO, tamanioServicio);
		Optional<Precio> precioDetalle = precioRepository.findByCategoriaAndValor(CategoriaEnum.DETALLE, detalleServicio);
		Optional<Precio> precioColor = precioRepository.findByCategoriaAndValor(CategoriaEnum.COLORACION, coloracionServicio);
		Optional<Precio> precioEstilo = precioRepository.findByCategoriaAndValor(CategoriaEnum.ESTILO, estiloServicio);

		/*Se extrae el "precio" (campo precioAdicional) que necesitamos para sumar*/
		BigDecimal pTipo = precioTipo.get().getPrecioAdicional();
		BigDecimal pZona = precioZona.get().getPrecioAdicional();
		BigDecimal pTamanio = precioTamanio.get().getPrecioAdicional();
		BigDecimal pDetalle = precioDetalle.get().getPrecioAdicional();
		BigDecimal pColor = precioColor.get().getPrecioAdicional();
		BigDecimal pEstilo = precioEstilo.get().getPrecioAdicional();
		
		/*Se suman todos los precios recuperados en el paso anterior*/
		/*Se usa add() como método de suma de la clase BigDecimal*/
		BigDecimal precioSinIva = pTipo.add(pZona).add(pTamanio).add(pDetalle).add(pColor).add(pEstilo);
		
		/*Añadimos el iva*/
		BigDecimal iva = precioSinIva.multiply(IVA_PORCENTAJE);
		BigDecimal precioConIva = precioSinIva.add(iva);
		
		/*Creamos objeto Presupuesto*/
		Presupuesto presupuesto = new Presupuesto (
				servicio.getIdServicio(), precioSinIva, iva, precioConIva, LocalDateTime.now(), true,Estado.PENDIENTE, null);


		/*Guardamos en BBDD*/
		Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);

		
		return presupuestoGuardado;
	}

	@Override
	public Presupuesto calcularPresupuestoPorId(int idServicio) {
		// Buscar servicio por ID
	    Optional<Servicio> servicioOpt = servicioRepository.findById(idServicio);
	    
	    if (servicioOpt.isPresent()) {
	        Servicio servicio = servicioOpt.get();
	        
	        return calcularPresupuesto(servicio);
	    } else {
	        throw new RuntimeException("Servicio no encontrado con ID: " + idServicio);
	    }
	}
}
