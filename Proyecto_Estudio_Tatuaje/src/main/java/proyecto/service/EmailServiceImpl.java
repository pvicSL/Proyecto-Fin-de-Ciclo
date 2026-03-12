package proyecto.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import proyecto.modelo.dto.ContactoDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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
    
 // --- DATOS DEL ESTUDIO ---
    private static final String ESTUDIO_NOMBRE = "INK & CO S.L.";
    private static final String ESTUDIO_DIR1  = "Calle Me Falta un Tornillo, 5, Local Bajo";
    private static final String ESTUDIO_DIR2  = "47195 Arroyo de la Encomienda (Valladolid), España";
    private static final String ESTUDIO_TEL   = "TEL: 621 89 78 27";
    private static final String ESTUDIO_EMAIL = "EMAIL: tatusyspruebas@gmail.com";
    private static final String ESTUDIO_CIF   = "CIF: 12345678Z";


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
    @Async("mailExecutor")
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
            		<!DOCTYPE html>
            		<html>
            		<head>
            		<meta charset="UTF-8">

            		<style>
            		body{
            		    font-family: Arial, sans-serif;
            		    background:#f2f2f2;
            		    margin:0;
            		    padding:0;
            		}

            		.container{
            		    max-width:600px;
            		    margin:40px auto;
            		    background:#ffffff;
            		    border-radius:10px;
            		    overflow:hidden;
            		    box-shadow:0 4px 12px rgba(0,0,0,0.1);
            		}

            		.header{
            		    background:#2f2f2f;
            		    padding:25px;
            		    text-align:center;
            		    color:white;
            		    font-size:26px;
            		    font-weight:bold;
            		    letter-spacing:3px;
            		}

            		.header span{
            		    color:#00c7b7;
            		}

            		.content{
            		    padding:35px;
            		    color:#333;
            		}

            		.title{
            		    font-size:22px;
            		    font-weight:bold;
            		    margin-bottom:15px;
            		}

            		.resume-box{
            		    background:#f4f4f4;
            		    border-left:4px solid #333;
            		    padding:20px;
            		    border-radius:6px;
            		    margin:25px 0;
            		}

            		.row{
            		    display:flex;
            		    justify-content:space-between;
            		    padding:8px 0;
            		    border-bottom:1px solid #e5e5e5;
            		}

            		.row:last-child{
            		    border-bottom:none;
            		}

            		.price{
            		    font-weight:bold;
            		}

            		.deposit{
            		    color:#008b8b;
            		    font-weight:bold;
            		}

            		.info{
            		    margin-top:20px;
            		    line-height:1.6;
            		}

            		.btn{
            		    display:block;
            		    width:fit-content;
            		    margin:30px auto;
            		    background:#138a84;
            		    color:white !important;
            		    padding:14px 35px;
            		    border-radius:6px;
            		    text-decoration:none;
            		    font-weight:bold;
            		    letter-spacing:1px;
            		}

            		.warning{
            		    background:#c61c3c;
            		    color:white;
            		    padding:18px;
            		    border-radius:6px;
            		    text-align:center;
            		    font-size:14px;
            		    margin-top:25px;
            		}

            		.footer{
            		    background:#efefef;
            		    text-align:center;
            		    padding:20px;
            		    font-size:13px;
            		    color:#666;
            		}
            		</style>

            		</head>

            		<body>

            		<div class="container">

            		<div class="header">
            		INK<span>&</span>CO
            		</div>

            		<div class="content">

            		<div class="title">¡Hola, %s!</div>

            		<p>
            		¡Buenas noticias! Tu solicitud ha sido revisada y estamos deseando verte por el estudio.
            		</p>

            		<p>Aquí tienes el resumen de tu cita:</p>

            		<div class="resume-box">

            		<div class="row">
            		<span>Localizador: </span>
            		<strong>%s</strong>
            		</div>

            		<div class="row">
            		<span>Presupuesto total (IVA incluido): </span>
            		<span class="price">%.2f €</span>
            		</div>

            		<div class="row">
            		<span>Fianza:</span>
            		<span class="deposit">%.2f €</span>
            		</div>

            		</div>

            		<div class="info">
            		<p>
            		<strong>IMPORTANTE:</strong> Si estás conforme con el presupuesto y quieres continuar,
            		deberás abonar una fianza de %.2f €.
            		</p>

            		<p>Esta cantidad se descontará del importe total.</p>

            		<p>Si tienes cualquier duda, responde a este correo.</p>
            		</div>

            		<a class="btn" href="%s">
            		PAGAR FIANZA
            		</a>

            		<div class="warning">
            		⚠ IMPORTANTE: dispones de 48 horas para realizar el pago.<br>
            		De lo contrario, la reserva se anulará automáticamente.
            		</div>

            		</div>

            		<div class="footer">
            		<strong>INK&CO Tattoo Studio</strong><br>
            		<p>C/ Calle Me Falta un Tornillo, 5, Local Bajo</p>
            		<p>47195 Arroyo de la Encomienda (Valladolid), España</p>
            		<p>TEL: 621 89 78 27</p>
            		<p>EMAIL: tatusyspruebas@gmail.com</p>
            		
            		</div>

            		</div>

            		</body>
            		</html>
            		""",
            		nombreCliente,
            		localizador,
            		precioTotal,
            		fianza,
            		fianza,
            		enlacePago
            		);

            helper.setText(htmlContent, true);

            ClassPathResource logo = new ClassPathResource("static/logo-placeholder.png");
            helper.addInline("logoEstudio", logo);
            javaMailSender.send(message);

            System.out.println("✅ Solicitud pago enviada");

        } catch (Exception e) {
            throw new RuntimeException("Error enviando solicitud pago", e);
        }
    }


    /* =====================================================
       RECUPERACIÓN PASSWORD
       ===================================================== */
    @Async("mailExecutor")
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

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f5f5f5;
                                margin: 0;
                                padding: 0;
                            }

                            .container {
                                max-width: 600px;
                                margin: 40px auto;
                                background-color: #ffffff;
                                border-radius: 8px;
                                overflow: hidden;
                                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                            }

                            .header {
                                background-color: #343434;
                                padding: 30px;
                                text-align: center;
                            }

                            .content {
                                padding: 40px 30px;
                                color: #333;
                                line-height: 1.6;
                            }

                            .h-title {
                                color: #343434;
                                font-size: 22px;
                                margin-bottom: 20px;
                                font-weight: bold;
                            }

                            .box-info {
                                background-color: #f8f9fa;
                                border-left: 5px solid #343434;
                                padding: 20px 25px;
                                border-radius: 4px;
                                text-align: center;
                            }

                            .btn-action {
                                display: block;
                                width: fit-content;
                                margin: 30px auto;
                                background-color: #008B8B;
                                color: #ffffff !important;
                                padding: 15px 35px;
                                text-decoration: none;
                                border-radius: 5px;
                                font-weight: bold;
                                letter-spacing: 1px;
                            }

                            .footer {
                                text-align: center;
                                font-size: 13px;
                                color: #777;
                                margin-top: 20px;
                            }
                        </style>
                    </head>

                    <body>
                        <div class="container">

                            <div class="header">
                                <img src="cid:logoEstudio"
                                     alt="TatuSys"
                                     style="max-height:70px;" />
                            </div>

                            <div class="content">

                                <p class="h-title">
                                    Recuperación de contraseña
                                </p>

                                <p>
                                    Hemos recibido una solicitud para restablecer tu contraseña.
                                </p>

                                <div class="box-info">
                                    Pulsa el botón inferior para crear una nueva contraseña.
                                </div>

                                <a class="btn-action" href="%s">
                                    RESTABLECER CONTRASEÑA
                                </a>

                                <div class="footer">
                                    Si no solicitaste este cambio, puedes ignorar este correo.
                                </div>

                            </div>
                        </div>
                    </body>
                    </html>
                    """.formatted(enlace);

            helper.setText(html, true);

            javaMailSender.send(message);

            System.out.println("✅ Email recuperación enviado");

        } catch (Exception e) {
            throw new RuntimeException("Error email recuperación", e);
        }
    }


    /* =====================================================
       CONFIRMACIÓN PASSWORD
       ===================================================== */
    @Async("mailExecutor")
    @Override
    public void enviarEmailConfirmacion(String destinatario, String mensaje) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = crearHelper(message);

            helper.setTo(destinatario);
            añadirCopiaEstudio(helper);

            helper.setSubject("TatuSys - Contraseña Actualizada");

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f5f5f5;
                                margin: 0;
                                padding: 0;
                            }

                            .container {
                                max-width: 600px;
                                margin: 40px auto;
                                background-color: #ffffff;
                                border-radius: 8px;
                                overflow: hidden;
                                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                            }

                            .header {
                                background-color: #343434;
                                padding: 30px;
                                text-align: center;
                            }

                            .content {
                                padding: 40px 30px;
                                color: #333;
                                line-height: 1.6;
                                text-align: center;
                            }

                            .h-title {
                                color: #343434;
                                font-size: 22px;
                                margin-bottom: 20px;
                                font-weight: bold;
                            }

                            .box-info {
                                background-color: #f8f9fa;
                                border-left: 5px solid #008B8B;
                                padding: 20px;
                                border-radius: 4px;
                                margin-top: 20px;
                            }

                            .footer {
                                margin-top: 25px;
                                font-size: 13px;
                                color: #777;
                            }
                        </style>
                    </head>

                    <body>
                        <div class="container">

                            <div class="header">
                                <img src="cid:logoEstudio"
                                     alt="TatuSys"
                                     style="max-height:70px;" />
                            </div>

                            <div class="content">

                                <p class="h-title">
                                    ✅ Contraseña actualizada correctamente
                                </p>

                                <div class="box-info">
                                    %s
                                </div>

                                <div class="footer">
                                    Si no realizaste este cambio,
                                    contacta con el estudio inmediatamente.
                                </div>

                            </div>

                        </div>
                    </body>
                    </html>
                    """.formatted(mensaje);

            helper.setText(html, true);

            ClassPathResource logo =
                    new ClassPathResource("static/logo-placeholder.png");
            helper.addInline("logoEstudio", logo);

            javaMailSender.send(message);

            System.out.println("✅ Email confirmación enviado");

        } catch (Exception e) {
            throw new RuntimeException("Error enviando email confirmación", e);
        }
    }


    /* =====================================================
       CONTACTO WEB
       ===================================================== */
    @Async("mailExecutor")
    @Override
    public void enviarMensajeContacto(ContactoDTO contacto) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {

            MimeMessageHelper helper = crearHelper(message);

            helper.setTo(emailEstudio);
            helper.setSubject(
                    "NUEVO MENSAJE WEB - " + contacto.getNombre()
            );

            String telefono =
                    contacto.getTelefono() != null &&
                    !contacto.getTelefono().isBlank()
                            ? contacto.getTelefono()
                            : "No proporcionado";

            String html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta charset="UTF-8">
                        <style>
                            body {
                                font-family: Arial, sans-serif;
                                background-color: #f5f5f5;
                                margin: 0;
                                padding: 0;
                            }

                            .container {
                                max-width: 600px;
                                margin: 40px auto;
                                background-color: #ffffff;
                                border-radius: 8px;
                                overflow: hidden;
                                box-shadow: 0 4px 10px rgba(0,0,0,0.1);
                            }

                            .header {
                                background-color: #343434;
                                padding: 30px;
                                text-align: center;
                            }

                            .content {
                                padding: 40px 30px;
                                color: #333;
                                line-height: 1.6;
                            }

                            .title {
                                font-size: 22px;
                                font-weight: bold;
                                margin-bottom: 25px;
                                color: #343434;
                            }

                            .box-info {
                                background-color: #f8f9fa;
                                border-left: 5px solid #008B8B;
                                padding: 20px;
                                border-radius: 4px;
                            }

                            .info-row {
                                margin-bottom: 12px;
                            }

                            .label {
                                color: #666;
                                font-size: 14px;
                            }

                            .value {
                                font-weight: bold;
                                color: #343434;
                            }

                            .mensaje {
                                margin-top: 20px;
                                padding-top: 15px;
                                border-top: 1px solid #eee;
                                white-space: pre-line;
                            }
                        </style>
                    </head>

                    <body>
                        <div class="container">

                            <div class="header">
                                <img src="cid:logoEstudio"
                                     alt="TatuSys"
                                     style="max-height:70px;" />
                            </div>

                            <div class="content">

                                <div class="title">
                                    📩 Nuevo mensaje desde la web
                                </div>

                                <div class="box-info">

                                    <div class="info-row">
                                        <span class="label">Nombre:</span><br>
                                        <span class="value">%s</span>
                                    </div>

                                    <div class="info-row">
                                        <span class="label">Email:</span><br>
                                        <span class="value">%s</span>
                                    </div>

                                    <div class="info-row">
                                        <span class="label">Teléfono:</span><br>
                                        <span class="value">%s</span>
                                    </div>

                                    <div class="mensaje">
                                        <span class="label">Mensaje:</span><br><br>
                                        %s
                                    </div>

                                </div>

                            </div>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    contacto.getNombre(),
                    contacto.getEmail(),
                    telefono,
                    contacto.getMensaje()
            );

            helper.setText(html, true);

            javaMailSender.send(message);

            System.out.println("✅ Mensaje contacto recibido");

        } catch (Exception e) {
            throw new RuntimeException("Error enviando mensaje contacto", e);
        }
    }


    /* =====================================================
       FACTURA
       ===================================================== */
    @Async("mailExecutor")
    @Override
    public void enviarFactura(String destinatario, String nombreCliente, byte[] pdfAdjunto) {

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = crearHelper(message);

            helper.setTo(destinatario);
            añadirCopiaEstudio(helper);
            helper.setSubject("INK&CO - Tu factura");

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Arial', sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0,0,0,0.1); }
                        .header { background-color: #343434; padding: 30px; text-align: center; }
                        .header img { max-height: 70px; }
                        .content { padding: 40px 30px; color: #333333; line-height: 1.6; }
                        .h-title { color: #343434; font-size: 22px; margin-bottom: 10px; font-weight: bold; }
                        .footer { background-color: #f8f9fa; border-top: 3px solid #008B8B; padding: 20px 30px; font-size: 13px; color: #666; }
                        .footer-row { margin-bottom: 4px; }
                        .footer-nombre { font-weight: bold; color: #343434; font-size: 14px; margin-bottom: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <img src="cid:logoEstudio" alt="INK&CO" />
                        </div>
                        <div class="content">
                            <p class="h-title">Hola, %s</p>
                            <p>Gracias por confiar en <strong>INK & CO</strong>. Adjuntamos la factura correspondiente al servicio realizado.</p>
                            <p>Si tienes cualquier duda, no dudes en contactarnos.</p>
                        </div>
                        <div class="footer">
                            <div class="footer-nombre">%s</div>
                            <div class="footer-row">%s</div>
                            <div class="footer-row">%s</div>
                            <div class="footer-row">%s</div>
                            <div class="footer-row">%s</div>
                            <div class="footer-row">%s</div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                    nombreCliente,
                    ESTUDIO_NOMBRE,
                    ESTUDIO_DIR1,
                    ESTUDIO_DIR2,
                    ESTUDIO_TEL,
                    ESTUDIO_EMAIL,
                    ESTUDIO_CIF
                );

            helper.setText(htmlContent, true);

            // Adjuntar PDF
            helper.addAttachment("factura.pdf", new ByteArrayResource(pdfAdjunto));

            // Embeber logo
            ClassPathResource logo = new ClassPathResource("static/logo-placeholder.png");
            helper.addInline("logoEstudio", logo);

            javaMailSender.send(message);
            System.out.println("✅ Factura enviada a cliente y estudio");

        } catch (Exception e) {
            throw new RuntimeException("Error enviando factura", e);
        }
    }
    }

