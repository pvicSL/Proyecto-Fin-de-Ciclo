package proyecto.service;

import java.math.BigDecimal;

public interface EmailService {

	void enviarSolicitudPago(String destinatarioReal, String nombreCliente, String localizador, 
            BigDecimal precioTotal, BigDecimal fianza);
	
	  void enviarEmailRecuperacion(String destinatario, String token);
	    
	  void enviarEmailConfirmacion(String destinatario, String mensaje);
}
