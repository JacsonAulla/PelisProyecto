package com.MediaWeseco.Back.service.impl;

import com.MediaWeseco.Back.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    // Inyectamos la librería de Spring Mail
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("tu_correo_real@gmail.com"); // Debe coincidir con application.properties
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            System.out.println("Email enviado exitosamente a: " + to);

        } catch (Exception e) {
            // Si falla, lo mostramos en consola pero no rompemos el programa
            // (O podrías lanzar una excepción si prefieres ser estricto)
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }
}