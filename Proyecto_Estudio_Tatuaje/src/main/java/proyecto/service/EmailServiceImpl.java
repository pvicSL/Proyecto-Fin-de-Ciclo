package proyecto.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import proyecto.modelo.dto.ContactoDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${spring.mail.username}")
    private String emailRemitente;

    @Value("${tatusys.email.estudio}")
    private String emailEstudio;

    @Autowired
    private JavaMailSender javaMailSender;

    private final String BASE_URL_FRONT = "http://localhost:4200";


    /* =====================================================
       MÉTODO CENTRALIZADO (CLAVE)
       ===================================================== */
    private MimeMessageHelper crearHelper(MimeMessage message)
            throws MessagingException {

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(emailRemitente);

        return helper;
    }

    private void añadirCopiaEstudio(MimeMessageHelper helper)
            throws MessagingException {

        helper.setCc(emailEstudio);
    }


    /* =====================================================
       SOLICITUD DE PAGO
       ===================================================== */
    @Override
    public void enviarSolicitudPago(String destinatarioReal,
                                    String nombreCliente,
                                    String localizador,
                                    BigDecimal precioTotal,
                                    BigDecimal fianza) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = crearHelper(message);

            helper.setTo(destinatarioReal);
            añadirCopiaEstudio(helper);

            helper.setSubject("INK&CO - Confirma tu cita " + localizador);

            String enlacePago =
                    BASE_URL_FRONT + "/pago-fianza/" + localizador;

            String htmlContent = String.format("""
                    <html>
                    <body>
                    <h2>Hola %s</h2>
                    <p>Tu cita ha sido aceptada.</p>
                    <p>Total: %.2f €</p>
                    <p>Fianza: %.2f €</p>
                    <a href="%s">PAGAR FIANZA</a>
                    </body>
                    </html>
                    """,
                    nombreCliente,
                    precioTotal,
                    fianza,
                    enlacePago);

            helper.setText(htmlContent, true);

            javaMailSender.send(message);

            System.out.println("✅ Solicitud pago enviada");

        } catch (Exception e) {
            throw new RuntimeException("Error enviando solicitud pago", e);
        }
    }


    /* =====================================================
       RECUPERACIÓN PASSWORD
       ===================================================== */
    @Override
    public void enviarEmailRecuperacion(String destinatario, String token) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = crearHelper(message);

            helper.setTo(destinatario);
            añadirCopiaEstudio(helper);

            helper.setSubject("TatuSys - Recuperación de Contraseña");

            String enlace =
                    BASE_URL_FRONT + "/reset-password?token=" + token;

            helper.setText("""
                    <h2>Recuperación de contraseña</h2>
                    <a href="%s">Restablecer contraseña</a>
                    """.formatted(enlace), true);

            javaMailSender.send(message);

            System.out.println("✅ Email recuperación enviado");

        } catch (Exception e) {
            throw new RuntimeException("Error email recuperación", e);
        }
    }


    /* =====================================================
       CONFIRMACIÓN PASSWORD
       ===================================================== */
    @Override
    public void enviarEmailConfirmacion(String destinatario,
                                        String mensaje) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(emailRemitente);
        message.setTo(destinatario);
        message.setCc(emailEstudio);
        message.setSubject("TatuSys - Contraseña Actualizada");
        message.setText(mensaje);

        javaMailSender.send(message);
    }


    /* =====================================================
       CONTACTO WEB
       ===================================================== */
    @Override
    public void enviarMensajeContacto(ContactoDTO contacto) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(emailRemitente);
        message.setTo(emailEstudio);

        message.setSubject(
                "NUEVO MENSAJE WEB - " + contacto.getNombre()
        );

        String telefono =
                contacto.getTelefono() != null
                        ? contacto.getTelefono()
                        : "No proporcionado";

        message.setText("""
                NUEVO MENSAJE WEB

                Nombre: %s
                Email: %s
                Teléfono: %s

                Mensaje:
                %s
                """.formatted(
                contacto.getNombre(),
                contacto.getEmail(),
                telefono,
                contacto.getMensaje()
        ));

        javaMailSender.send(message);

        System.out.println("✅ Mensaje contacto recibido");
    }


    /* =====================================================
       FACTURA
       ===================================================== */
    @Override
    public void enviarFactura(String destinatario,
                              String nombreCliente,
                              byte[] pdfAdjunto) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = crearHelper(message);

            helper.setTo(destinatario);
            añadirCopiaEstudio(helper);

            helper.setSubject("INK&CO - Tu factura");

            helper.setText(
                    "Adjuntamos la factura del servicio realizado.",
                    false
            );

            helper.addAttachment(
                    "factura.pdf",
                    new ByteArrayResource(pdfAdjunto)
            );

            javaMailSender.send(message);

            System.out.println("✅ Factura enviada a cliente y estudio");

        } catch (Exception e) {
            throw new RuntimeException("Error enviando factura", e);
        }
    }
}
