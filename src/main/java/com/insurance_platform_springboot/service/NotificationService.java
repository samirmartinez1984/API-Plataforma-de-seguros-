package com.insurance_platform_springboot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Servicio dedicado a la gestión y envío de notificaciones a los usuarios.
 *
 * <p>Este servicio centraliza la lógica de comunicación para desacoplarla
 * de los servicios de negocio principales. Utiliza la ejecución asíncrona
 * para no impactar el rendimiento de las respuestas de la API.</p>
 *
 * <p>Dependencias:</p>
 * <ul>
 *     <li>{@link JavaMailSender}: Interfaz de Spring para el envío de correos electrónicos.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;

    /**
     * Envía un correo electrónico de bienvenida a un nuevo usuario de forma asíncrona.
     *
     * <p>La anotación {@link Async} asegura que la operación se ejecute en un hilo
     * separado, liberando inmediatamente el hilo de la solicitud principal y mejorando
     * la experiencia del usuario durante el registro.</p>
     *
     * @param recipientEmail La dirección de correo electrónico del destinatario.
     * @param userName El nombre del usuario para personalizar el mensaje.
     */
    @Async
    public void sendWelcomeEmail(String recipientEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("¡Bienvenido a la Plataforma de Seguros!");
            message.setText("Hola " + userName + ",\n\n" +
                    "Tu registro en nuestra plataforma ha sido exitoso. " +
                    "Ya puedes empezar a explorar nuestro catálogo de productos y encontrar el seguro que mejor se adapte a tus necesidades.\n\n" +
                    "Gracias por unirte a nosotros.\n\n" +
                    "Saludos cordiales,\n" +
                    "El equipo de la Plataforma de Seguros");

            mailSender.send(message);
            log.info("Correo de bienvenida enviado exitosamente a: {}", recipientEmail);
        } catch (Exception e) {
            log.error("Error al enviar el correo de bienvenida a {}: {}", recipientEmail, e.getMessage());
        }
    }
}