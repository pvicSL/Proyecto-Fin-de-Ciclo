package proyecto.service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyecto.modelo.dto.CitaAdminDTO;
import proyecto.modelo.dto.CitaPresupuestoDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.enums.Estado;
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
	CitaRepository citaRepository;

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
		// 1. Necesitamos recuperar la Cita para saber qué zona, tamaño, etc. tiene
	    Cita cita = citaRepository.findById(presupuesto.getIdServicio())
	            .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

	    /* 2. Recalcular los precios desde la tabla 'precios' usando la Cita */
	    BigDecimal pBase = precioRepository.findByCategoriaAndValor(CategoriaEnum.BASE, "SERVICIO_BASE").get().getPrecioAdicional();
	    BigDecimal pTipo = precioRepository.findByCategoriaAndValor(CategoriaEnum.TIPO, cita.getTipo().toString()).get().getPrecioAdicional();
	    BigDecimal pZona = precioRepository.findByCategoriaAndValor(CategoriaEnum.ZONA, cita.getZona().toString()).get().getPrecioAdicional();
	    BigDecimal pTamanio = precioRepository.findByCategoriaAndValor(CategoriaEnum.TAMANIO, cita.getTamanio().toString()).get().getPrecioAdicional();
	    BigDecimal pDetalle = precioRepository.findByCategoriaAndValor(CategoriaEnum.DETALLE, cita.getDetalle().toString()).get().getPrecioAdicional();
	    BigDecimal pColor = precioRepository.findByCategoriaAndValor(CategoriaEnum.COLORACION, cita.getColoracion().toString()).get().getPrecioAdicional();
	    BigDecimal pEstilo = precioRepository.findByCategoriaAndValor(CategoriaEnum.ESTILO, cita.getEstilo().toString()).get().getPrecioAdicional();

	    // Suma de los nuevos valores de la tabla precios
	    BigDecimal nuevaSumaServicios = pBase.add(pTipo).add(pZona).add(pTamanio).add(pDetalle).add(pColor).add(pEstilo);

	    /* 3. Lógica del Precio Extra */
	    // Mantenemos el precioExtra que ya tiene el objeto presupuesto (si es null, 0)
	    BigDecimal pExtraActual = (presupuesto.getPrecioExtra() != null) ? presupuesto.getPrecioExtra() : BigDecimal.ZERO;
	    String presupuestoComentarios = (presupuesto.getComentarios() != null) ? presupuesto.getComentarios() : null;
	    
	    /* Recuperamos los comentarios o los cambiamos por los nuevos */
	    

	    /* 4. Cálculos Finales */
	    BigDecimal subtotalConExtra = nuevaSumaServicios.add(pExtraActual);
	    BigDecimal nuevoIva = subtotalConExtra.multiply(IVA_PORCENTAJE);
	    BigDecimal nuevoPrecioFinal = subtotalConExtra.add(nuevoIva);

	    /* 5. Actualizar el objeto que recibimos por argumento */
	    presupuesto.setPrecioBase(nuevaSumaServicios);
	    presupuesto.setPrecioExtra(pExtraActual); // Aseguramos que no sea null
	    presupuesto.setIva(nuevoIva);
	    presupuesto.setPrecioFinal(nuevoPrecioFinal);
	    presupuesto.setFecha(LocalDateTime.now());
	    presupuesto.setComentarios(presupuestoComentarios);
	    presupuesto.setEstado(Estado.GENERADO);

	    // 6. Guardar cambios
	    return presupuestoRepository.save(presupuesto);
	}

	
	

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
	    
	    /*LÓGICA DE RECALCULO: Buscar si ya existe un presupuesto para esta cita */
	    BigDecimal pExtra = BigDecimal.ZERO;
	    String comentariosPresupuesto = null;

	    /*Se suman todos los precios recuperados en el paso anterior*/
	    BigDecimal precioSinIva = pBase.add(pTipo).add(pZona).add(pTamanio).add(pDetalle).add(pColor).add(pEstilo);
	    BigDecimal subtotalConExtra = precioSinIva.add(pExtra);

	    /*Añadimos el iva*/
	    BigDecimal iva = subtotalConExtra.multiply(IVA_PORCENTAJE);
	    BigDecimal precioConIva = subtotalConExtra.add(iva);

	    Presupuesto presupuesto = new Presupuesto(
	                cita.getIdCita(), precioSinIva, pExtra, iva, precioConIva, 
	                LocalDateTime.now(), true, Estado.PENDIENTE, comentariosPresupuesto);

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
	public Presupuesto buscarUnPresupuestoPorIdCita(int idCita) {
		// Buscamos en el repositorio. Usamos .orElse(null) o lanzamos una excepción 
	    return presupuestoRepository.findByIdServicio(idCita)
	            .stream() // Convertimos a stream por si hay varios
	            .findFirst() // Tomamos solo uno para evitar el error de "11 results"
	            .orElseThrow(() -> new NoSuchElementException("No se encontró presupuesto para la cita: " + idCita));
	}

	@Override
	public List<CitaPresupuestoDTO> obtenerCitasPorEstadoPresupuesto(Estado estado) {
		List<Presupuesto> presupuestos = presupuestoRepository.findByEstado(estado);
	    
	    List<Integer> idsCitas = presupuestos.stream()
	                                         .map(Presupuesto::getIdServicio)
	                                         .distinct()
	                                         .toList();

	    List<Cita> todasLasCitas = citaRepository.findAllById(idsCitas);

	    return presupuestos.stream()
	        .map(p -> {
	            // Buscamos la cita que le corresponde a este presupuesto
	            Cita citaMatch = todasLasCitas.stream()
	                .filter(c -> c.getIdCita() == p.getIdServicio())
	                .findFirst()
	                .orElse(null);

	            // Usamos el constructor de conveniencia que creamos
	            return new CitaPresupuestoDTO(citaMatch, p);
	        })
	        .collect(Collectors.toList());
	}
}
