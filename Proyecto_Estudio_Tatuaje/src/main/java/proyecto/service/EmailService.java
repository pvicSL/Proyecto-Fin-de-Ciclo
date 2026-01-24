package proyecto.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    // URL del Frontend (Angular)
    private final String BASE_URL_FRONT = "http://localhost:4200"; 

    public void enviarSolicitudPago(String destinatarioReal, String nombreCliente, String localizador, 
                                    BigDecimal precioTotal, BigDecimal fianza) {
        
        MimeMessage message = javaMailSender.createMimeMessage();
        
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("no-reply@inkandco.com"); 
            
            // En Mailtrap, esto sirve para ver en la interfaz "para quién iba dirigido", 
            // aunque el correo se queda en la bandeja de sandbox.
            helper.setTo(destinatarioReal); 
            
            System.out.println(">>> [MAILTRAP] Generando email para: " + destinatarioReal + " (Ref: " + localizador + ")");

            helper.setSubject("INK&CO - Confirma tu cita " + localizador);

            String enlacePago = BASE_URL_FRONT + "/pago-fianza/" + localizador;

            // HTML CORPORATIVO
            String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Arial', sans-serif; background-color: #f5f5f5; margin: 0; padding: 0; }
                        .container { max-width: 600px; margin: 40px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1); }
                        .header { background-color: #343434; padding: 30px; text-align: center; }
                        .logo { color: #ffffff; font-size: 28px; font-weight: bold; letter-spacing: 3px; text-decoration: none; }
                        .logo span { color: #00CCCC; }
                        .content { padding: 40px 30px; color: #333333; line-height: 1.6; }
                        .h-title { color: #343434; font-size: 22px; margin-bottom: 20px; font-weight: bold; }
                        .box-info { background-color: #f8f9fa; border-left: 5px solid #343434; padding: 20px; margin: 25px 0; border-radius: 4px; }
                        .info-row { display: flex; justify-content: space-between; margin-bottom: 10px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
                        .info-label { color: #666; font-size: 14px; }
                        .info-val { color: #343434; font-weight: bold; font-size: 16px; }
                        .btn-pay { display: block; width: fit-content; margin: 30px auto; background-color: #008B8B; color: #ffffff !important; padding: 15px 35px; text-decoration: none; border-radius: 5px; font-weight: bold; letter-spacing: 1px; text-align: center; cursor: pointer; }
                        .btn-pay:hover { background-color: #00CCCC; }
                        .alert { background-color: #C41E3A; color: white; padding: 15px; border-radius: 5px; font-size: 13px; margin-top: 30px; font-weight: bold; text-align: center; }
                        .footer { background-color: #eeeeee; text-align: center; padding: 20px; font-size: 12px; color: #777777; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header"><div class="logo">INK<span>&</span>CO</div></div>
                        <div class="content">
                            <p class="h-title">¡Hola, %s!</p>
                            <p>¡Buenas noticias! Tu solicitud ha sido revisada y aceptada.</p>
                            <div class="box-info">
                                <div class="info-row"><span class="info-label">Localizador:</span><span class="info-val">%s</span></div>
                                <div class="info-row"><span class="info-label">Total:</span><span class="info-val">%s €</span></div>
                                <div class="info-row" style="border-bottom: none;"><span class="info-label">Fianza:</span><span class="info-val" style="color: #008B8B;">%s €</span></div>
                            </div>
                            <p>Para confirmar tu cita, abona la fianza:</p>
                            <a href="%s" class="btn-pay">PAGAR FIANZA</a>
                            <div class="alert">⚠️ Tienes 48 horas. Si no realizas el pago, la cita se liberará automáticamente.</div>
                        </div>
                        <div class="footer"><p><strong>INK&CO Tattoo Studio</strong></p></div>
                    </div>
                </body>
                </html>
                """, nombreCliente, localizador, precioTotal, fianza, enlacePago);

            helper.setText(htmlContent, true);
            javaMailSender.send(message);
            
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println(">>> ERROR MAIL: " + e.getMessage());
        }
    }
}

