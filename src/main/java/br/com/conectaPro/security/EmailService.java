package br.com.conectaPro.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void sendRecoveryEmail(
            String to,
            String token) {

        try {

            String link =
                    "http://localhost:5173/reset-password?token="
                            + token;

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8");

            helper.setFrom(
                    from,
                    "ConectaPro");

            helper.setTo(to);

            helper.setSubject(
                    "Recuperação de senha - ConectaPro");

            helper.setText(
                    """
                    Você solicitou recuperação de senha.

                    Clique no link abaixo:

                    %s

                    O link expira em 30 minutos.

                    Caso não tenha solicitado esta alteração,
                    ignore este e-mail.
                    """
                            .formatted(link));

            mailSender.send(message);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao enviar e-mail",
                    e);
        }
    }
}