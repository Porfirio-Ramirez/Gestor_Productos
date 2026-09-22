package Proyecto.Gestor_Productos.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
/**
 * Servicio de envío de correos de texto plano, usado por {@link AuthService}
 * para notificaciones de seguridad, verificación de email y recuperación
 * de contraseña. Envuelve {@link JavaMailSender} (configurado vía SMTP en
 * application.properties) en una interfaz simple de un solo método.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    /** Dirección remitente, tomada de la misma cuenta configurada para el envío SMTP. */
    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envía un correo de texto plano.
     * @param to destinatario
     * @param subject asunto del correo
     * @param body cuerpo del mensaje en texto plano
     */
    public void sendSimpleEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}