package proyecto.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.enums.Estatus;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.PrecioRepository;
import proyecto.modelo.repository.PresupuestoRepository;


@Service
public class PresupuestoServiceImplJpaMy8 implements PresupuestoService{

	
	private static final BigDecimal IVA_PORCENTAJE = new BigDecimal("0.21"); // 21%
	
	@Autowired
	private PresupuestoRepository presupuestoRepository;
	
	@Autowired
	private PrecioRepository precioRepository;
	
	@Autowired
	private CitaRepository citaRepository;

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

	
		//TODO: refactorizar calcularPresupuesto aprovechando el método obtenerPreciosIndividuales(Cita cita)
	@Override
	public Presupuesto calcularPresupuesto(Cita cita) {

	    /*Extraer valores de la cita y almacenar en variables*/
	    String tipoServicio = cita.getTipo().toString();
	    String zonaServicio = cita.getZona().toString();
	    String tamanioServicio = cita.getTamanio().toString();
	    String detalleServicio = cita.getDetalle().toString();
	    String coloracionServicio = cita.getColoracion().toString();
	    String estiloServicio = cita.getEstilo().toString();

	    /*Se obtiene el objeto Precio completo de la BBDD*/
	    Optional<Precio> precioBase = precioRepository.findByCategoriaAndValor(CategoriaEnum.BASE, "SERVICIO_BASE");
	    Optional<Precio> precioTipo = precioRepository.findByCategoriaAndValor(CategoriaEnum.TIPO, tipoServicio);
	    Optional<Precio> precioZona = precioRepository.findByCategoriaAndValor(CategoriaEnum.ZONA, zonaServicio);
	    Optional<Precio> precioTamanio = precioRepository.findByCategoriaAndValor(CategoriaEnum.TAMANIO, tamanioServicio);
	    Optional<Precio> precioDetalle = precioRepository.findByCategoriaAndValor(CategoriaEnum.DETALLE, detalleServicio);
	    Optional<Precio> precioColor = precioRepository.findByCategoriaAndValor(CategoriaEnum.COLORACION, coloracionServicio);
	    Optional<Precio> precioEstilo = precioRepository.findByCategoriaAndValor(CategoriaEnum.ESTILO, estiloServicio);

	    /*Se extrae el "precio" (campo precioAdicional) que necesitamos para sumar*/
	    BigDecimal pBase = precioBase.get().getPrecioAdicional();
	    BigDecimal pTipo = precioTipo.get().getPrecioAdicional();
	    BigDecimal pZona = precioZona.get().getPrecioAdicional();
	    BigDecimal pTamanio = precioTamanio.get().getPrecioAdicional();
	    BigDecimal pDetalle = precioDetalle.get().getPrecioAdicional();
	    BigDecimal pColor = precioColor.get().getPrecioAdicional();
	    BigDecimal pEstilo = precioEstilo.get().getPrecioAdicional();

	    /*Se suman todos los precios recuperados en el paso anterior*/
	    BigDecimal precioSinIva = pBase.add(pTipo).add(pZona).add(pTamanio).add(pDetalle).add(pColor).add(pEstilo);

	    /*Añadimos el iva*/
	    BigDecimal iva = precioSinIva.multiply(IVA_PORCENTAJE);
	    BigDecimal precioConIva = precioSinIva.add(iva);

	    /*Creamos objeto Presupuesto*/
	    Presupuesto presupuesto = new Presupuesto (
	            cita.getIdCita(), precioSinIva, iva, precioConIva, LocalDateTime.now(), true, Estado.PENDIENTE, null);

	    /*Guardamos en BBDD*/
	    Presupuesto presupuestoGuardado = presupuestoRepository.save(presupuesto);

	    return presupuestoGuardado;
	}

	@Override
	public Presupuesto calcularPresupuestoPorId(int idCita) {
	    Optional<Cita> citaOpt = citaRepository.findById(idCita);
	    
	    if (citaOpt.isPresent()) {
	        Cita cita = citaOpt.get();
	        return calcularPresupuesto(cita);
	    } else {
	        throw new RuntimeException("Cita no encontrada con ID: " + idCita);
	    }
	}

	@Override
	public Map<String, BigDecimal> obtenerPreciosIndividuales(Cita cita) {
	    
	    // Reutilizar la misma lógica que ya tienes
	    String tipoServicio = cita.getTipo().toString();
	    String zonaServicio = cita.getZona().toString();
	    String tamanioServicio = cita.getTamanio().toString();
	    String detalleServicio = cita.getDetalle().toString();
	    String coloracionServicio = cita.getColoracion().toString();
	    String estiloServicio = cita.getEstilo().toString();

	    // Obtener precios (mismo código que ya tienes)
	    Optional<Precio> precioBase = precioRepository.findByCategoriaAndValor(CategoriaEnum.BASE, "SERVICIO_BASE");
	    Optional<Precio> precioTipo = precioRepository.findByCategoriaAndValor(CategoriaEnum.TIPO, tipoServicio);
	    Optional<Precio> precioZona = precioRepository.findByCategoriaAndValor(CategoriaEnum.ZONA, zonaServicio);
	    Optional<Precio> precioTamanio = precioRepository.findByCategoriaAndValor(CategoriaEnum.TAMANIO, tamanioServicio);
	    Optional<Precio> precioDetalle = precioRepository.findByCategoriaAndValor(CategoriaEnum.DETALLE, detalleServicio);
	    Optional<Precio> precioColor = precioRepository.findByCategoriaAndValor(CategoriaEnum.COLORACION, coloracionServicio);
	    Optional<Precio> precioEstilo = precioRepository.findByCategoriaAndValor(CategoriaEnum.ESTILO, estiloServicio);

	    // Crear mapa con los precios
	    Map<String, BigDecimal> precios = new HashMap<>();
	    precios.put("BASE", precioBase.get().getPrecioAdicional());
	    precios.put("TIPO", precioTipo.get().getPrecioAdicional());
	    precios.put("ZONA", precioZona.get().getPrecioAdicional());
	    precios.put("TAMANIO", precioTamanio.get().getPrecioAdicional());
	    precios.put("DETALLE", precioDetalle.get().getPrecioAdicional());
	    precios.put("COLORACION", precioColor.get().getPrecioAdicional());
	    precios.put("ESTILO", precioEstilo.get().getPrecioAdicional());
	    
	    return precios;
	}
	
	@Override
	public PreciosIndividualesDTO obtenerPreciosCompletosConIva(int idCita) {
	    // 1. Buscar cita
	    Cita cita = citaRepository.findById(idCita).orElse(null);
	    if (cita == null) return null;
	    
	    // 2. Usar método existente
	    Map<String, BigDecimal> precios = obtenerPreciosIndividuales(cita);
	    
	    // 3. Calcular subtotal, IVA y total
	    BigDecimal subtotal = precios.values().stream()
	                                 .reduce(BigDecimal.ZERO, BigDecimal::add);
	    BigDecimal iva = subtotal.multiply(IVA_PORCENTAJE);
	    BigDecimal total = subtotal.add(iva);
	    
	    // 4. Crear y devolver DTO
	    PreciosIndividualesDTO resultado = new PreciosIndividualesDTO();
	    resultado.setPrecioBase(precios.get("BASE"));
	    resultado.setPrecioTipo(precios.get("TIPO"));
	    resultado.setPrecioZona(precios.get("ZONA"));
	    resultado.setPrecioTamanio(precios.get("TAMANIO"));
	    resultado.setPrecioDetalle(precios.get("DETALLE"));
	    resultado.setPrecioColoracion(precios.get("COLORACION"));
	    resultado.setPrecioEstilo(precios.get("ESTILO"));
	    resultado.setSubtotal(subtotal);
	    resultado.setIva(iva);
	    resultado.setTotal(total);
	    
	    return resultado;
	}
	
	@Override
	public BigDecimal[] calcularSoloValores(Cita cita) {
	    /*Extraer valores de la cita y almacenar en variables*/
	    String tipoServicio = cita.getTipo().toString();
	    String zonaServicio = cita.getZona().toString();
	    String tamanioServicio = cita.getTamanio().toString();
	    String detalleServicio = cita.getDetalle().toString();
	    String coloracionServicio = cita.getColoracion().toString();
	    String estiloServicio = cita.getEstilo().toString();

	    /*Se obtiene el objeto Precio completo de la BBDD*/
	    Optional<Precio> precioBase = precioRepository.findByCategoriaAndValor(CategoriaEnum.BASE, "SERVICIO_BASE");
	    Optional<Precio> precioTipo = precioRepository.findByCategoriaAndValor(CategoriaEnum.TIPO, tipoServicio);
	    Optional<Precio> precioZona = precioRepository.findByCategoriaAndValor(CategoriaEnum.ZONA, zonaServicio);
	    Optional<Precio> precioTamanio = precioRepository.findByCategoriaAndValor(CategoriaEnum.TAMANIO, tamanioServicio);
	    Optional<Precio> precioDetalle = precioRepository.findByCategoriaAndValor(CategoriaEnum.DETALLE, detalleServicio);
	    Optional<Precio> precioColor = precioRepository.findByCategoriaAndValor(CategoriaEnum.COLORACION, coloracionServicio);
	    Optional<Precio> precioEstilo = precioRepository.findByCategoriaAndValor(CategoriaEnum.ESTILO, estiloServicio);

	    /*Se extrae el "precio" (campo precioAdicional) que necesitamos para sumar*/
	    BigDecimal pBase = precioBase.get().getPrecioAdicional();
	    BigDecimal pTipo = precioTipo.get().getPrecioAdicional();
	    BigDecimal pZona = precioZona.get().getPrecioAdicional();
	    BigDecimal pTamanio = precioTamanio.get().getPrecioAdicional();
	    BigDecimal pDetalle = precioDetalle.get().getPrecioAdicional();
	    BigDecimal pColor = precioColor.get().getPrecioAdicional();
	    BigDecimal pEstilo = precioEstilo.get().getPrecioAdicional();

	    /*Se suman todos los precios recuperados en el paso anterior*/
	    BigDecimal precioSinIva = pBase.add(pTipo).add(pZona).add(pTamanio).add(pDetalle).add(pColor).add(pEstilo);

	    /*Añadimos el iva*/
	    BigDecimal iva = precioSinIva.multiply(IVA_PORCENTAJE);
	    BigDecimal precioConIva = precioSinIva.add(iva);

	    // SOLO devolver los valores calculados, SIN guardar
	    return new BigDecimal[]{precioSinIva, iva, precioConIva};
	}
	
	public boolean aceptarPresupuesto(Cita cita) {
	    // Buscar presupuesto por ID de cita
	    Optional<Presupuesto> presupuestoOpt = presupuestoRepository.findByIdServicio(cita.getIdCita());

	    // Si no existe, lanzar excepción
	    if (!presupuestoOpt.isPresent()) {
	        throw new IllegalArgumentException("No se encontró presupuesto para la cita con ID: " + cita.getIdCita());
	    }

	    Presupuesto presupuesto = presupuestoOpt.get();

	    // Validar que esté en estado GENERADO
	    if (presupuesto.getEstado() != Estado.GENERADO) {
	        throw new IllegalStateException("El presupuesto debe estar en estado GENERADO para poder ser aceptado. Estado actual: " + presupuesto.getEstado());
	    }

	    // Cambiar estado del presupuesto y guardar
	    presupuesto.setEstado(Estado.ACEPTADO);
	    presupuestoRepository.save(presupuesto);

	    // Cambiar estatus de la cita a CONFIRMADO y guardar
	    cita.setEstatus(Estatus.CONFIRMADO);
	    citaRepository.save(cita);

	    return true;
	}

	public boolean rechazarPresupuesto(Cita cita) {
	    // Buscar presupuesto por ID de cita
	    Optional<Presupuesto> presupuestoOpt = presupuestoRepository.findByIdServicio(cita.getIdCita());
	    
	    // Si no existe, lanzar excepción
	    if (!presupuestoOpt.isPresent()) {
	        throw new IllegalArgumentException("No se encontró presupuesto para la cita con ID: " + cita.getIdCita());
	    }
	    
	    Presupuesto presupuesto = presupuestoOpt.get();
	    
	    // Validar que esté en estado GENERADO
	    if (presupuesto.getEstado() != Estado.GENERADO) {
	        throw new IllegalStateException("El presupuesto debe estar en estado GENERADO para poder ser rechazado. Estado actual: " + presupuesto.getEstado());
	    }
	    
	    // Cambiar estado y guardar
	    presupuesto.setEstado(Estado.RECHAZADO);
	    presupuestoRepository.save(presupuesto);
	    
	    return true;
	}
	
}
