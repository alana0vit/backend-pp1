package br.com.conectaPro.security;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendRecoveryEmail(
            String to,
            String token) {

        String link =
            "http://localhost:5173/reset-password?token="
                + token;

        SimpleMailMessage message =
            new SimpleMailMessage();

        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Recuperação de senha");

        message.setText(
                """
                Você solicitou recuperação de senha.

                Clique no link abaixo:

                %s

                O link expira em 30 minutos.
                """
                        .formatted(link));

        mailSender.send(message);
    }
}