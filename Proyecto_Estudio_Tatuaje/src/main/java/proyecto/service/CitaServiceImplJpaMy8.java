package proyecto.service;

import java.math.BigDecimal;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import proyecto.modelo.dto.CitaAdminDTO;
import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Cliente;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.enums.Coloracion;
import proyecto.modelo.enums.Detalle;
import proyecto.modelo.enums.Estatus;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.ClienteRepository;
import proyecto.modelo.repository.PrecioRepository;
import proyecto.modelo.repository.PresupuestoRepository;

@Service
public class CitaServiceImplJpaMy8 implements CitaService {

	@Autowired
	private CitaRepository citaRepository;

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
    private PresupuestoRepository presupuestoRepository;
    @Autowired
    private PrecioRepository precioRepository;

	@Override
	public List<Cita> leerTodos() {
		return citaRepository.findAll();
	}

	@Override
	public Cita buscarUnaCita(int idCita) {
		return citaRepository.findById(idCita).orElse(null);
	}

	@Override
	public Integer calcularDuracion(Cita cita) {
		int duracionBase = 30;
		switch (cita.getTamanio()) {
		case MINI:
			duracionBase = 60;
		case PEQUEÑO:
			duracionBase = 90;
		case MEDIANO:
			duracionBase = 120;
		case GRANDE:
			duracionBase = 180;
		case MUY_GRANDE:
			duracionBase = 240;
		}

		if (cita.getDetalle() == Detalle.DENSO)
			duracionBase += 30;
		if (cita.getColoracion() == Coloracion.COLOR)
			duracionBase += 30;

		return duracionBase;
	}

	// --- MÉTODO CREAR CITA MODIFICADO ---
	@Override
	public Cita crearCita(Cita cita, List<MultipartFile> ficheros) {

		// GENERAR REFERENCIA ÚNICA
		// Generamos una parte aleatoria del UUID y la ponemos en mayúsculas (Ej:
		// "A5B6F1C2"). Se añade comprobacion para evitar duplicados. 
		if (cita.getReferencia() == null || cita.getReferencia().isEmpty()) {
		    String codigo;
		    do {
		        codigo = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
		    } while (citaRepository.existsByReferencia(codigo));
		    cita.setReferencia(codigo);
		}

		// 1. GUARDAR LAS IMÁGENES EN DISCO (Si las hay)
		if (ficheros != null && !ficheros.isEmpty()) {
			try {
				// Iteramos los ficheros (Máximo 3 según tu lógica)
				for (int i = 0; i < ficheros.size(); i++) {
					MultipartFile archivo = ficheros.get(i);
					if (!archivo.isEmpty()) {
						String nombreUnico = guardarArchivo(archivo); // Llamada al método auxiliar de abajo

						// Asignamos a la columna correspondiente según el orden
						if (i == 0)
							cita.setImagenRef1(nombreUnico);
						if (i == 1)
							cita.setImagenRef2(nombreUnico);
						if (i == 2)
							cita.setImagenRef3(nombreUnico);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				// Podrías lanzar una excepción personalizada si falla la subida
			}
		}

		// 2. GESTIÓN DEL CLIENTE (IGUAL QUE ANTES)
		Cliente clienteDelFormulario = cita.getCliente();

		if (clienteDelFormulario != null) {
			Optional<Cliente> clienteExistenteOpt = clienteRepository.findByEmail(clienteDelFormulario.getEmail());

			if (clienteExistenteOpt.isPresent()) {
				Cliente clienteBD = clienteExistenteOpt.get();
				clienteBD.setNombre(clienteDelFormulario.getNombre());
				clienteBD.setApellido1(clienteDelFormulario.getApellido1());
				clienteBD.setApellido2(clienteDelFormulario.getApellido2());
				clienteBD.setTelefono(clienteDelFormulario.getTelefono());
				clienteBD.setDocumentoIdentificacion(clienteDelFormulario.getDocumentoIdentificacion());
				clienteRepository.save(clienteBD);
				cita.setCliente(clienteBD);
			} else {
				Cliente nuevoCliente = clienteRepository.save(clienteDelFormulario);
				cita.setCliente(nuevoCliente);
			}
		}

		// 3. CALCULAR DURACIÓN
		Integer duracion = calcularDuracion(cita);
		cita.setDuracionMinutos(duracion);

		return citaRepository.save(cita);
	}

	// --- MÉTODO AUXILIAR PARA GUARDAR EN DISCO ---
	private String guardarArchivo(MultipartFile archivo) throws IOException {
		// Nombre carpeta donde se guardan (Créala en la raíz de tu proyecto o usa ruta
		// absoluta)
		String carpetaUploads = "uploads";

		// Creamos la carpeta si no existe
		Path rutaCarpeta = Paths.get(carpetaUploads);
		if (!Files.exists(rutaCarpeta)) {
			Files.createDirectories(rutaCarpeta);
		}

		// Generamos nombre único para evitar que "foto.jpg" sobrescriba otra "foto.jpg"
		String nombreOriginal = archivo.getOriginalFilename();
		String extension = "";
		if (nombreOriginal != null && nombreOriginal.contains(".")) {
			extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
		}
		String nombreUnico = UUID.randomUUID().toString() + extension;

		// Guardamos el fichero
		Path rutaCompleta = rutaCarpeta.resolve(nombreUnico);
		Files.copy(archivo.getInputStream(), rutaCompleta);

		return nombreUnico; // Devolvemos el nombre para guardarlo en la BBDD
	}
	// ----------------------------------------------

	/*
	 * METODO CREAR CITA PREVIO A MODIFICACION PARA SUBIR IMAGENES
	 * 
	 * @Override public Cita crearCita(Cita cita) {
	 * 
	 * // 1. GESTIÓN DEL CLIENTE (Lógica de Actualización / Creación) // Sacamos el
	 * cliente que viene del formulario Cliente clienteDelFormulario =
	 * cita.getCliente();
	 * 
	 * if (clienteDelFormulario != null) { // Buscamos si ya existe por email
	 * Optional<Cliente> clienteExistenteOpt =
	 * clienteRepository.findByEmail(clienteDelFormulario.getEmail());
	 * 
	 * if (clienteExistenteOpt.isPresent()) { // CASO A: EL CLIENTE YA EXISTE ->
	 * ACTUALIZAMOS SUS DATOS Cliente clienteBD = clienteExistenteOpt.get();
	 * 
	 * // Sobreescribimos los datos antiguos con los nuevos que ha escrito el
	 * usuario clienteBD.setNombre(clienteDelFormulario.getNombre());
	 * clienteBD.setApellido1(clienteDelFormulario.getApellido1());
	 * clienteBD.setApellido2(clienteDelFormulario.getApellido2());
	 * clienteBD.setTelefono(clienteDelFormulario.getTelefono());
	 * clienteBD.setDocumentoIdentificacion(clienteDelFormulario.
	 * getDocumentoIdentificacion());
	 * 
	 * // Importante: Guardamos el cliente actualizado en la BBDD
	 * clienteRepository.save(clienteBD);
	 * 
	 * // Asignamos este cliente (ya actualizado) a la cita
	 * cita.setCliente(clienteBD);
	 * 
	 * } else { // CASO B: EL CLIENTE ES NUEVO -> LO CREAMOS DE CERO Cliente
	 * nuevoCliente = clienteRepository.save(clienteDelFormulario);
	 * cita.setCliente(nuevoCliente); } } /* // 1. Buscar y asignar cliente if
	 * (cita.getCliente() != null && cita.getCliente().getIdCliente() != 0) { int
	 * idCliente = cita.getCliente().getIdCliente(); Cliente clienteCompleto =
	 * clienteRepository.findById(idCliente).orElse(null);
	 * cita.setCliente(clienteCompleto); }
	 * 
	 * 
	 * // 2. Calcular y asignar duración automáticamente Integer duracion =
	 * calcularDuracion(cita); cita.setDuracionMinutos(duracion);
	 * 
	 * return citaRepository.save(cita); }
	 */

	@Override
	public int eliminarCita(int idCita) {
		if (citaRepository.existsById(idCita)) {
			citaRepository.deleteById(idCita);
			return 1; // Retornamos 1 para indicar Éxito
		}
		return 0; // Retornamos 0 si no existía
	}

	@Override
	public Cita actualizarCita(Cita citaConDatosNuevos) {

		// 1. Primero buscamos la cita ORIGINAL en la base de datos
		Optional<Cita> citaOriginalOpt = citaRepository.findById(citaConDatosNuevos.getIdCita());

		if (citaOriginalOpt.isPresent()) {
			Cita citaDeLaBBDD = citaOriginalOpt.get();

			// 2. Modificamos SOLO lo que nos interesa (Fecha y Hora)
			// Mantenemos el Cliente, el Tipo, el Estilo, etc. intactos.
			if (citaConDatosNuevos.getFecha() != null) {
				citaDeLaBBDD.setFecha(citaConDatosNuevos.getFecha());
			}
			if (citaConDatosNuevos.getHora() != null) {
				citaDeLaBBDD.setHora(citaConDatosNuevos.getHora());
			}

			// Opcional: Si el front enviase más datos, los actualizamos aquí...
			// Pero como solo cambias fecha, con esto basta para que no explote.

			// 3. Guardamos la cita original modificada
			return citaRepository.save(citaDeLaBBDD);
		} else {
			// Si no existe el ID, devolvemos null
			return null;
		}
	}
	/*
	 * METODO QUE TENÍAS ANTES; EXPLOTABA AL MODIFICAR UNA FECHA DE CITA DESDE EL
	 * FRONT // 1. Verificamos si la cita existe Cita citaExistente =
	 * citaRepository.findById(cita.getIdCita()).orElse(null);
	 * 
	 * if (citaExistente != null) { // 2. Actualizamos los campos que el usuario
	 * puede cambiar (fecha y hora) citaExistente.setFecha(cita.getFecha());
	 * citaExistente.setHora(cita.getHora());
	 * 
	 * // También podrías actualizar otros campos si fuera necesario
	 * citaExistente.setEstatus(cita.getEstatus());
	 * citaExistente.setComentarios(cita.getComentarios());
	 * 
	 * // 3. Importante: si cambian parámetros del tatuaje, podría recalcular la //
	 * duración // Aunque por ahora, para el mockup de fechas, solo necesitamos
	 * fecha y hora.
	 * citaExistente.setDuracionMinutos(calcularDuracion(citaExistente));
	 * 
	 * // 4. Guardamos los cambios return citaRepository.save(citaExistente); }
	 * 
	 * return null; }
	 */

	@Override
	public Optional<Cita> buscarPorCliente(String email) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	// CRUD básico con DTOs
	@Override
	public List<CitaDTO> listarCitasDTO() {
		List<Cita> citas = citaRepository.findAll();
		return citas.stream().map(cita -> new CitaDTO(cita)) // ← Constructor DTO hace la conversión
				.collect(Collectors.toList());
	}

	@Override
	public CitaDTO obtenerCitaDTOPorId(int idCita) {
		Optional<Cita> citaOpt = citaRepository.findById(idCita);

		if (citaOpt.isPresent()) {
			return new CitaDTO(citaOpt.get()); // ← Convertir entidad → DTO
		} else {
			return null; // O lanzar excepción personalizada
		}
	}

	@Override
	public List<Cita> findByFecha(LocalDate fecha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<String>> buscarHuecosDisponibles(int duracionMinutos) {
		Map<String, List<String>> calendario = new LinkedHashMap<>();

		// CONFIGURACIÓN DEL ESTUDIO (Ajustar según necesidad)
		LocalTime horaApertura = LocalTime.of(10, 0);
		LocalTime horaCierre = LocalTime.of(20, 0);
		int intervaloMinutos = 30; // Saltos del selector de hora

		LocalDate diaActual = LocalDate.now().plusDays(1); // Empezamos a buscar desde mañana

		// Revisamos los próximos 7 días
		// CAMBIADO A 30 DÍAS, HABRÁ QUE DECIDIR A CUÁNTOS MESES VISTA
		// TIENEN ABIERTA LA AGENDA DE CITAS EN EL ESTUDIO.
		for (int i = 0; i < 30; i++) {
			LocalDate fechaRevision = diaActual.plusDays(i);
			List<String> huecosDia = new ArrayList<>();

			// 1. Obtenemos las citas que YA existen ese día
			List<Cita> citasOcupadas = citaRepository.findByFecha(fechaRevision);

			// 2. Iteramos desde la apertura hasta el cierre
			LocalTime horaIteracion = horaApertura;

			// El bucle termina cuando la hora + duración supera el cierre
			while (horaIteracion.plusMinutes(duracionMinutos).isBefore(horaCierre)
					|| horaIteracion.plusMinutes(duracionMinutos).equals(horaCierre)) {

				// Verificamos si este hueco choca con alguna cita existente
				if (esHuecoLibre(horaIteracion, duracionMinutos, citasOcupadas)) {
					huecosDia.add(horaIteracion.format(DateTimeFormatter.ofPattern("HH:mm")));
				}

				// Saltamos al siguiente intervalo
				horaIteracion = horaIteracion.plusMinutes(intervaloMinutos);
			}

			// Si hay huecos, los guardamos en el mapa con la fecha como clave
			if (!huecosDia.isEmpty()) {
				calendario.put(fechaRevision.toString(), huecosDia);
			}
		}

		return calendario;
	}

	// MÉTODO AUXILIAR PRIVADO PARA COMPROBAR COLISIONES
	private boolean esHuecoLibre(LocalTime horaInicio, int duracion, List<Cita> citasOcupadas) {
		LocalTime horaFin = horaInicio.plusMinutes(duracion);

		for (Cita cita : citasOcupadas) {
			LocalTime inicioCita = cita.getHora();
			// Asegurarse de que la entidad Cita tiene el campo 'duracionMinutos' bien
			// guardado
			// Si es null, usar un valor por defecto para evitar error (ej. 60)
			int duracionCita = (cita.getDuracionMinutos() != null) ? cita.getDuracionMinutos() : 60;

			LocalTime finCita = inicioCita.plusMinutes(duracionCita);

			// Lógica de solapamiento:
			// Un hueco está ocupado si empieza antes de que acabe la otra cita
			// Y termina después de que empiece la otra cita.
			if (horaInicio.isBefore(finCita) && horaFin.isAfter(inicioCita)) {
				return false; // Colisión detectada
			}
		}
		return true; // El hueco está limpio
	}

	@Override
	public List<CitaDTO> obtenerPorRango(LocalDate fecha, String vista) {
		LocalDate inicio = fecha;
	    LocalDate fin = vista.equals("dia") ? fecha : fecha.plusDays(6);
	    
	    return citaRepository.findByEstatusAndFechaBetween(Estatus.CONFIRMADO, inicio, fin)
	                         .stream()
	                         .map(cita -> new CitaDTO(cita)) // método de conversión
	                         .collect(Collectors.toList());
	}

	@Override
	public List<CitaDTO> obtenerPorEstatus(Estatus estatus) {
		// TODO Auto-generated method stub
		return citaRepository.findByEstatus(estatus.PENDIENTE);
	}

	@Override
	public CitaAdminDTO obtenerDetalleCita(int idServicio) {
		// 1. Recuperamos la Cita (Servicio)
        Cita cita = citaRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + idServicio));

        // 2. Recuperamos el Cliente (asumiendo que Cita tiene un objeto Cliente o id_cliente)
        Cliente cliente = cita.getCliente();

        // 3. Recuperamos el Presupuesto ya existente vinculado a esta cita
        Presupuesto presupuesto = presupuestoRepository.findById(idServicio)
                .orElseThrow(() -> new RuntimeException("No existe un presupuesto para la cita: " + idServicio));

        // 4. Construimos el DTO mapeando los datos de las 3 entidades
        CitaAdminDTO dto = new CitaAdminDTO();
        
        // Datos Identificadores
        dto.setIdServicio(cita.getIdCita());
        dto.setIdPresupuesto(presupuesto.getIdPresupuesto());

        // Datos Cliente
        dto.setNombre(cliente.getNombre());
        dto.setApellido1(cliente.getApellido1());
        dto.setApellido1(cliente.getApellido2());
        dto.setTelefono(cliente.getTelefono());
        dto.setEmail(cliente.getEmail());
        dto.setDni(cliente.getDocumentoIdentificacion());

        // Datos del Servicio + Consulta de Precios (para el desglose visual en Angular)
        // Usamos .name() para convertir el Enum a String
        dto.setTipo(cita.getTipo().name()); 
        dto.setPrecioTipo(obtenerMonto(CategoriaEnum.TIPO, cita.getTipo().name()));

        dto.setZona(cita.getZona().name());
        dto.setPrecioZona(obtenerMonto(CategoriaEnum.ZONA, cita.getZona().name()));

        dto.setTamanio(cita.getTamanio().name());
        dto.setPrecioTamanio(obtenerMonto(CategoriaEnum.TAMANIO, cita.getTamanio().name()));

        dto.setDetalle(cita.getDetalle().name());
        dto.setPrecioDetalle(obtenerMonto(CategoriaEnum.DETALLE, cita.getDetalle().name()));

        dto.setColoracion(cita.getColoracion().name());
        dto.setPrecioColoracion(obtenerMonto(CategoriaEnum.COLORACION, cita.getColoracion().name()));

        dto.setEstilo(cita.getEstilo().name());
        dto.setPrecioEstilo(obtenerMonto(CategoriaEnum.ESTILO, cita.getEstilo().name()));

        dto.setComentariosServicio(cita.getComentarios());

        // Datos del Presupuesto (cogemos los datos que YA están en la tabla presupuestos)
        dto.setPrecioBase(presupuesto.getPrecioBase());
        dto.setIva(presupuesto.getIva());
        dto.setPrecioFinal(presupuesto.getPrecioFinal());
        dto.setFechaPresupuesto(presupuesto.getFecha());
        dto.setEstadoPresupuesto(presupuesto.getEstado().name());
        dto.setComentariosPresupuesto(presupuesto.getComentarios());

        return dto;
	}


	private BigDecimal obtenerMonto(CategoriaEnum cat, String valor) {
		// TODO Auto-generated method stub
		return null;
	}

	


	public Optional<Cita> buscarPorReferenciaYEmail(String referencia, String email) {

		// 1. Pequeña validación de seguridad (fail-fast)
		if (referencia == null || email == null) {
			return Optional.empty();
		}

		// 2. Limpieza de datos (Trim para quitar espacios accidentales al copiar/pegar)
		String refLimpia = referencia.trim();
		String emailLimpio = email.trim();

		// 3. Llamada al repositorio
		return citaRepository.findByReferenciaAndCliente_Email(refLimpia, emailLimpio);
	}

}
