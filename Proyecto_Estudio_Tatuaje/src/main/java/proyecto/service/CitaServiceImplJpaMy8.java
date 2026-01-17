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


import proyecto.modelo.dto.CitaCompletaDTO;
import proyecto.modelo.dto.CitaDTO;
import proyecto.modelo.dto.CitaModificacionDTO;
import proyecto.modelo.dto.PreciosIndividualesDTO;
import proyecto.modelo.entities.Cita;
import proyecto.modelo.entities.Cliente;
import proyecto.modelo.entities.Precio;
import proyecto.modelo.entities.Presupuesto;
import proyecto.modelo.entities.Trabajador;
import proyecto.modelo.enums.CategoriaEnum;
import proyecto.modelo.enums.Coloracion;
import proyecto.modelo.enums.Detalle;
import proyecto.modelo.enums.Estado;
import proyecto.modelo.enums.Estatus;
import proyecto.modelo.enums.Estilo;
import proyecto.modelo.enums.Funciones;
import proyecto.modelo.enums.Tamanio;
import proyecto.modelo.enums.Tipo;
import proyecto.modelo.enums.Zona;
import proyecto.modelo.repository.CitaRepository;
import proyecto.modelo.repository.ClienteRepository;
import proyecto.modelo.repository.PrecioRepository;
import proyecto.modelo.repository.PresupuestoRepository;
import proyecto.modelo.repository.TrabajadorRepository;

@Service
public class CitaServiceImplJpaMy8 implements CitaService {

	@Autowired
	private CitaRepository citaRepository;

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
    private PresupuestoRepository presupuestoRepository;
 
    @Autowired
    private PresupuestoService presupuestoService;
    
    @Autowired  
    private TrabajadorRepository trabajadorRepository;
    
    

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
	    int duracionBase = 120; 

	    if (cita.getTamanio() != null) {
	        switch (cita.getTamanio()) {
	            case MINI:
	                duracionBase = 60;
	                break; 
	            case PEQUEÑO:
	                duracionBase = 90;
	                break; 
	            case MEDIANO:
	                duracionBase = 120;
	                break; 
	            case GRANDE:
	                duracionBase = 180;
	                break; 
	            case MUY_GRANDE:
	                duracionBase = 240;
	                break; 
	            default:
	                duracionBase = 120;
	        }
	    }

	    if (cita.getDetalle() == proyecto.modelo.enums.Detalle.DENSO) {
	        duracionBase += 30;
	    }
	    
	    if (cita.getColoracion() == proyecto.modelo.enums.Coloracion.COLOR) {
	        duracionBase += 30;
	    }

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

		//4. NUEVO: ASIGNACIÓN DE TRABAJADOR (Si no viene del front, lo calculamos aquí por seguridad)
        if (cita.getTrabajador() == null) {
             Trabajador trabajadorAsignado = seleccionarTrabajadorAutomatico(cita.getTipo(), cita.getEstilo());
             cita.setTrabajador(trabajadorAsignado);
        }

        // 5. GUARDAR CITA
        Cita citaGuardada = citaRepository.save(cita);

        // 6. NUEVO: GENERAR PRESUPUESTO AUTOMÁTICO
        // Esto creará una fila en la tabla 'presupuestos' vinculada a esta cita
        presupuestoService.calcularPresupuesto(citaGuardada);

        return citaGuardada;
    }
	
    public Trabajador seleccionarTrabajadorAutomatico(Tipo tipoServicio, Estilo estilo) {
        Funciones funcionRequerida = null;

        // ---------------------------------------------------------------
        // ZONA PREPARADA PARA FUTURA ESPECIFICIDAD (ESTILOS Y TIPOS)
        // ---------------------------------------------------------------
        /* // TODO: DESCOMENTAR CUANDO EL ENUM 'Funciones' TENGA MÁS VALORES
        
        // 1. Prioridad por Estilo (Ejemplo)
        if (estilo == Estilo.JAPONES) {
             // funcionRequerida = Funciones.ESPECIALISTA_JAPONES;
        } 
        else if (estilo == Estilo.FINELINE) {
             // funcionRequerida = Funciones.ESPECIALISTA_FINELINE;
        }
        else if (tipoServicio == Tipo.COVER) {
             // funcionRequerida = Funciones.ESPECIALISTA_COVERS;
        }
        */
     // ---------------------------------------------------------------
        // LÓGICA ACTUAL (GENÉRICA: CREACIÓN vs ELIMINACIÓN)
        // ---------------------------------------------------------------
        // Si no se ha asignado una función específica arriba, usamos la genérica
        if (funcionRequerida == null) {
            if (tipoServicio == Tipo.ELIMINACION) {
                funcionRequerida = Funciones.ELIMINACION;
            } else {
                // Tatuaje, Cover, Retoque -> Todos van a CREACION por ahora
                funcionRequerida = Funciones.CREACION;
            }
        }

        // 2. Buscar en BBDD trabajadores con esa función
        List<Trabajador> candidatos = trabajadorRepository.findByFunciones(funcionRequerida);

        if (candidatos.isEmpty()) {
            // Manejo de error si no hay nadie (puedes lanzar excepción o devolver null)
            throw new RuntimeException("No hay trabajadores disponibles para la función: " + funcionRequerida);
        }

        // 3. ASIGNACIÓN (Round Robin simplificado / Aleatorio)
        // Aquí podrías mejorar buscando el que tenga menos citas ese día.
        int indiceAleatorio = (int) (Math.random() * candidatos.size());
        return candidatos.get(indiceAleatorio);
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
			return null;
		}
	}

	@Override
	public List<Cita> findByFecha(LocalDate fecha) {
		// TODO Auto-generated method stub
		return null;
	}

	/*@Override
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
	}*/
	
	@Override
    public Map<String, List<String>> buscarHuecosDisponibles(int duracionMinutos, int idTrabajador) {
        Map<String, List<String>> calendario = new LinkedHashMap<>();

        // ... (Tu configuración de horas apertura/cierre sigue igual) ...
        LocalTime horaApertura = LocalTime.of(10, 0);
        LocalTime horaCierre = LocalTime.of(20, 0);
        int intervaloMinutos = 30;
        LocalDate diaActual = LocalDate.now().plusDays(1);

        for (int i = 0; i < 30; i++) {
            LocalDate fechaRevision = diaActual.plusDays(i);
            List<String> huecosDia = new ArrayList<>();

            // CAMBIO CRÍTICO: Buscar citas SOLO de ese trabajador
            // Necesitas crear este método en CitaRepository si no existe:
            // List<Cita> findByTrabajadorIdTrabajadorAndFecha(int idTrabajador, LocalDate fecha);
            List<Cita> citasOcupadas = citaRepository.findByTrabajadorIdTrabajadorAndFecha(idTrabajador, fechaRevision);

            LocalTime horaIteracion = horaApertura;

            while (horaIteracion.plusMinutes(duracionMinutos).isBefore(horaCierre)
                    || horaIteracion.plusMinutes(duracionMinutos).equals(horaCierre)) {

                if (esHuecoLibre(horaIteracion, duracionMinutos, citasOcupadas)) {
                    huecosDia.add(horaIteracion.format(DateTimeFormatter.ofPattern("HH:mm")));
                }
                horaIteracion = horaIteracion.plusMinutes(intervaloMinutos);
            }

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

	    // Obtener las citas de la base de datos
	    List<Cita> citas = citaRepository.findByEstatusAndFechaBetween(Estatus.CONFIRMADO, inicio, fin);
	    
	    // Crear lista para los DTOs
	    List<CitaDTO> citasDTO = new ArrayList<>();
	    
	    // Convertir cada Cita a CitaDTO usando bucle 
	    for (Cita cita : citas) {
	        CitaDTO citaDTO = new CitaDTO(cita);
	        citasDTO.add(citaDTO);
	    }
	    
	    return citasDTO;
	}

	@Override
	public List<CitaDTO> obtenerPorEstatus(Estatus estatus) {
		return citaRepository.findByEstatus(estatus);
	}
	
	public List<CitaDTO> obtenerPorEstadoPresupuesto(Estado estadoPresupuesto) {
	    return citaRepository.obtenerPorEstadoPresupuesto(estadoPresupuesto);
	}
	

	public CitaCompletaDTO obtenerCitaCompleta(int id) {
	    // 1. Obtener los datos básicos (sin precios individuales)
	    CitaCompletaDTO cita = citaRepository.findCitaCompletaById(id);
	    
	    if (cita != null) {
	        // 2. Obtener los precios individuales usando el método existente
	        PreciosIndividualesDTO precios = presupuestoService.obtenerPreciosCompletosConIva(id);
	        
	        if (precios != null) {
	            // 3. Rellenar los precios individuales en el DTO
	            cita.setPrecioTipo(precios.getPrecioTipo());
	            cita.setPrecioZona(precios.getPrecioZona());
	            cita.setPrecioTamanio(precios.getPrecioTamanio());
	            cita.setPrecioDetalle(precios.getPrecioDetalle());
	            cita.setPrecioColoracion(precios.getPrecioColoracion());
	            cita.setPrecioEstilo(precios.getPrecioEstilo());
	        }
	    }
	    
	    return cita;
	}
	
	public CitaCompletaDTO actualizarCitaCompleta(int id, CitaCompletaDTO citaEditada) {
	    // 1. Buscar la cita existente
	    Cita cita = citaRepository.findById(id).orElse(null);
	    if (cita == null) return null;
	    
	    // 2. ACTUALIZAR CAMPOS DE LA CITA
	    cita.setTipo(Tipo.valueOf(citaEditada.getTipo()));
	    cita.setZona(Zona.valueOf(citaEditada.getZona()));
	    cita.setTamanio(Tamanio.valueOf(citaEditada.getTamanio()));
	    cita.setDetalle(Detalle.valueOf(citaEditada.getDetalle()));
	    cita.setColoracion(Coloracion.valueOf(citaEditada.getColoracion()));
	    cita.setEstilo(Estilo.valueOf(citaEditada.getEstilo()));
	    cita.setFecha(citaEditada.getFecha());
	    cita.setHora(citaEditada.getHora());
	    cita.setComentarios(citaEditada.getComentarios());
	    cita.setImagenRef1(citaEditada.getImagenRef1());
	    cita.setImagenRef2(citaEditada.getImagenRef2());
	    cita.setImagenRef3(citaEditada.getImagenRef3());
	    
	    // 3. ACTUALIZAR CAMPOS DEL CLIENTE
	    Cliente cliente = cita.getCliente();
	    cliente.setNombre(citaEditada.getClienteNombre());
	    cliente.setApellido1(citaEditada.getClienteApellido1());
	    cliente.setApellido2(citaEditada.getClienteApellido2());
	    cliente.setEmail(citaEditada.getClienteEmail());
	    cliente.setTelefono(citaEditada.getClienteTelefono());
	    cliente.setDocumentoIdentificacion(citaEditada.getClienteDocumentoIdentificacion());
	    
	    // 4. GUARDAR CAMBIOS
	    clienteRepository.save(cliente);
	    citaRepository.save(cita);
	    
	    // 5. Calcular nuevos valores del presupuesto
	    BigDecimal[] valores = presupuestoService.calcularSoloValores(cita);
	    
	    // 6. Actualizar presupuesto existente
	    Optional<Presupuesto> presupuestoOpt = presupuestoRepository.findByIdServicio(id);
	    if (presupuestoOpt.isPresent()) {
	        Presupuesto presupuestoExistente = presupuestoOpt.get();
	        presupuestoExistente.setPrecioBase(valores[0]);
	        presupuestoExistente.setIva(valores[1]);
	        presupuestoExistente.setPrecioFinal(valores[2]);
	        presupuestoExistente.setEstado(Estado.GENERADO);
	        presupuestoExistente.setComentarios(citaEditada.getPresupuestoComentarios());
	        presupuestoRepository.save(presupuestoExistente);
	    } else {
	        Presupuesto nuevoPresupuesto = presupuestoService.calcularPresupuesto(cita);
	        nuevoPresupuesto.setComentarios(citaEditada.getPresupuestoComentarios());
	    }
	    
	    // 7. Devolver datos actualizados CON precios individuales
	    CitaCompletaDTO resultado = citaRepository.findCitaCompletaById(id);
	    if (resultado != null) {
	        PreciosIndividualesDTO precios = presupuestoService.obtenerPreciosCompletosConIva(id);
	        if (precios != null) {
	            resultado.setPrecioTipo(precios.getPrecioTipo());
	            resultado.setPrecioZona(precios.getPrecioZona());
	            resultado.setPrecioTamanio(precios.getPrecioTamanio());
	            resultado.setPrecioDetalle(precios.getPrecioDetalle());
	            resultado.setPrecioColoracion(precios.getPrecioColoracion());
	            resultado.setPrecioEstilo(precios.getPrecioEstilo());
	        }
	    }
	    return resultado;
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
		return citaRepository.findByReferenciaAndClienteEmail(refLimpia, emailLimpio);
	}

	@Override
	public PreciosIndividualesDTO obtenerPreciosIndividualesPorCita(int idCita) {
	    return presupuestoService.obtenerPreciosCompletosConIva(idCita);
	}

    @Override
    public boolean modificarFechaCita(CitaModificacionDTO datos) {
        // Buscamos la cita asegurándonos de que el email coincide (seguridad)
        Optional<Cita> citaOpt = citaRepository.findByReferenciaAndClienteEmail(
            datos.getReferencia(), 
            datos.getEmail().toLowerCase() // Normalizamos el email por si acaso
        );

        if (citaOpt.isPresent()) {
            Cita cita = citaOpt.get();
            cita.setFecha(datos.getFecha());
            cita.setHora(datos.getHora());
            // Si quieres resetear el estatus a PENDIENTE al cambiar fecha, hazlo aquí:
            // cita.setEstatus(Estatus.PENDIENTE); 
            
            citaRepository.save(cita);
            return true;
        }
        return false;
    }

    @Override
    public boolean cancelarCitaPorReferencia(String referencia, String email) {
        Optional<Cita> citaOpt = citaRepository.findByReferenciaAndClienteEmail(
            referencia, 
            email.toLowerCase()
        );

        if (citaOpt.isPresent()) {
            // OPCIÓN A: Borrado físico (DELETE) - Tal como lo tienes ahora
            citaRepository.delete(citaOpt.get());
            
            // OPCIÓN B (Recomendada a futuro): Borrado lógico
            // Cita cita = citaOpt.get();
            // cita.setEstatus(Estatus.CANCELADO); // Requiere añadir CANCELADO al Enum
            // citaRepository.save(cita);

            return true;
        }
        return false;
    }
    

    private Precio encontrarPorCategoriaValor(CategoriaEnum categoria, String valor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public String asignarTrabajador(int citaId, int trabajadorId) {
        // 1. Buscar la cita
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (!citaOpt.isPresent()) {
            return "Error: No se encontró la cita con ID " + citaId;
        }
        Cita cita = citaOpt.get();
        
        // 2. Buscar el trabajador
        Optional<Trabajador> trabajadorOpt = trabajadorRepository.findById(trabajadorId);
        if (!trabajadorOpt.isPresent()) {
            return "Error: No se encontró el trabajador con ID " + trabajadorId;
        }
        Trabajador trabajador = trabajadorOpt.get();
        
        // 3. Verificar que la cita no esté ya asignada
        if (cita.getTrabajador() != null) {
            return "Error: La cita ya está asignada al trabajador " + cita.getTrabajador().getNombre();
        }
        
        // 4. Validar funciones del trabajador
        if (!validarFuncionTrabajador(cita.getTipo(), trabajador.getFunciones())) {
            return "Error: El trabajador " + trabajador.getNombre() + " no puede realizar servicios de tipo " + cita.getTipo();
        }
        
        // 5. Verificar disponibilidad horaria
        if (!verificarDisponibilidad(trabajador, cita)) {
            return "Error: El trabajador " + trabajador.getNombre() + " no está disponible en esa fecha y hora";
        }
        
        // 6. Asignar trabajador (bidireccional)
        cita.setTrabajador(trabajador);
        trabajador.getCitas().add(cita);
        
        // 7. Guardar cambios
        citaRepository.save(cita);
        trabajadorRepository.save(trabajador);
        
        // 8. Mensaje de éxito
        return "Cita con referencia " + cita.getReferencia() + " asignada correctamente al trabajador " + trabajador.getNombre() + " " + trabajador.getApellido1();
    }

    @Override
    public String desasignarTrabajador(int citaId) {
        // 1. Buscar la cita
        Optional<Cita> citaOpt = citaRepository.findById(citaId);
        if (!citaOpt.isPresent()) {
            return "Error: No se encontró la cita con ID " + citaId;
        }
        Cita cita = citaOpt.get();
        
        // 2. Verificar que la cita esté asignada
        if (cita.getTrabajador() == null) {
            return "Error: La cita no tiene ningún trabajador asignado";
        }
        
        // 3. Guardar info del trabajador para el mensaje
        Trabajador trabajador = cita.getTrabajador();
        String nombreTrabajador = trabajador.getNombre() + " " + trabajador.getApellido1();
        
        // 4. Desasignar (bidireccional)
        trabajador.getCitas().remove(cita);
        cita.setTrabajador(null);
        
        // 5. Guardar cambios
        trabajadorRepository.save(trabajador);
        citaRepository.save(cita);
        
        // 6. Mensaje de éxito
        return "Cita con referencia " + cita.getReferencia() + " desasignada correctamente del trabajador " + nombreTrabajador;
    }

    // Métodos auxiliares privados
    private boolean validarFuncionTrabajador(Tipo tipoServicio, Funciones funcionTrabajador) {
        if (funcionTrabajador == Funciones.ELIMINACION) {
            return tipoServicio == Tipo.ELIMINACION;
        } else if (funcionTrabajador == Funciones.CREACION) {
            return tipoServicio == Tipo.TATUAJE || tipoServicio == Tipo.COVER || tipoServicio == Tipo.RETOQUE;
        }
        return false;
    }

    private boolean verificarDisponibilidad(Trabajador trabajador, Cita citaAAsignar) {
        LocalDate fecha = citaAAsignar.getFecha();
        LocalTime horaInicio = citaAAsignar.getHora();
        LocalTime horaFin = horaInicio.plusMinutes(citaAAsignar.getDuracionMinutos());
        
        // Buscar citas del trabajador en la misma fecha
        List<Cita> citasDelTrabajador = trabajador.getCitas().stream()
            .filter(cita -> fecha.equals(cita.getFecha()))
            .collect(Collectors.toList());
        
        // Verificar solapamientos
        for (Cita cita : citasDelTrabajador) {
            LocalTime inicioCitaExistente = cita.getHora();
            LocalTime finCitaExistente = inicioCitaExistente.plusMinutes(cita.getDuracionMinutos());
            
            // Verificar solapamiento
            if (horaInicio.isBefore(finCitaExistente) && horaFin.isAfter(inicioCitaExistente)) {
                return false; // Hay solapamiento
            }
        }
        
        return true; // No hay conflictos
    }

	
	@Override
	public void aceptarPresupuesto(Cita cita) {
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
	    
	    // Cambiar estado y guardar
	    presupuesto.setEstado(Estado.ACEPTADO);
	    presupuestoRepository.save(presupuesto);
	}

	@Override
	public void rechazarPresupuesto(Cita cita) {
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
	}




}
