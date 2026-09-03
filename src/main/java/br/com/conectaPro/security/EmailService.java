package br.com.conectaPro.security;

import br.com.conectaPro.model.demand.Demand;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String from;

  private void enviar(String para, String assunto, String corpo) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(from, "ConectaPro");
      helper.setTo(para);
      helper.setSubject(assunto);
      helper.setText(corpo, true); // true = HTML habilitado
      mailSender.send(message);
    } catch (Exception e) {
      // Loga mas não deixa o fluxo principal falhar por causa do e-mail
      System.err.println("Erro ao enviar e-mail para " + para + ": " + e.getMessage());
    }
  }

  public void sendRecoveryEmail(String to, String token) {
    String link = "http://localhost:5173/reset-password?token=" + token;
    String corpo =
        """
                <p>Você solicitou a recuperação de senha na <strong>ConectaPro</strong>.</p>
                <p>Clique no link abaixo para redefinir sua senha:</p>
                <p><a href="%s">%s</a></p>
                <p>O link expira em <strong>30 minutos</strong>.</p>
                <p>Caso não tenha solicitado esta alteração, ignore este e-mail.</p>
                """
            .formatted(link, link);
    enviar(to, "Recuperação de senha - ConectaPro", corpo);
  }

  public void notificarNovaDemanda(Demand demand) {
    String para = demand.getProfessionalId().getEmail();
    String nomeProfissional = demand.getProfessionalId().getName();
    String nomeCliente = demand.getClientId().getName();
    String titulo = demand.getTitle();
    String codigo = demand.getCode();

    String corpo =
        """
                <p>Olá, <strong>%s</strong>!</p>
                <p>Você recebeu uma nova solicitação de serviço na <strong>ConectaPro</strong>.</p>
                <table>
                  <tr><td><strong>Código:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Serviço:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Cliente:</strong></td><td>%s</td></tr>
                </table>
                <p>Acesse o app para aceitar ou recusar. Você tem <strong>1 hora</strong> para responder antes que a demanda expire.</p>
                """
            .formatted(nomeProfissional, codigo, titulo, nomeCliente);

    enviar(para, "Nova solicitação de serviço [%s] - ConectaPro".formatted(codigo), corpo);
  }

  public void notificarDemandaAceita(Demand demand) {
    String para = demand.getClientId().getEmail();
    String nomeCliente = demand.getClientId().getName();
    String nomeProfissional = demand.getProfessionalId().getName();
    String titulo = demand.getTitle();
    String codigo = demand.getCode();

    String corpo =
        """
                <p>Olá, <strong>%s</strong>!</p>
                <p>Boas notícias! O profissional <strong>%s</strong> aceitou sua solicitação.</p>
                <table>
                  <tr><td><strong>Código:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Serviço:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Status:</strong></td><td>Aguardando execução</td></tr>
                </table>
                <p>Acompanhe o andamento pelo app.</p>
                """
            .formatted(nomeCliente, nomeProfissional, codigo, titulo);

    enviar(para, "Solicitação aceita [%s] - ConectaPro".formatted(codigo), corpo);
  }

  public void notificarDemandaRejeitada(Demand demand) {
    String para = demand.getClientId().getEmail();
    String nomeCliente = demand.getClientId().getName();
    String nomeProfissional = demand.getProfessionalId().getName();
    String titulo = demand.getTitle();
    String codigo = demand.getCode();

    String corpo =
        """
                <p>Olá, <strong>%s</strong>!</p>
                <p>Infelizmente o profissional <strong>%s</strong> não pôde atender sua solicitação.</p>
                <table>
                  <tr><td><strong>Código:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Serviço:</strong></td><td>%s</td></tr>
                </table>
                <p>Acesse o app para escolher outro profissional.</p>
                """
            .formatted(nomeCliente, nomeProfissional, codigo, titulo);

    enviar(para, "Solicitação recusada [%s] - ConectaPro".formatted(codigo), corpo);
  }

  public void notificarDemandaFechada(Demand demand) {
    String para = demand.getClientId().getEmail();
    String nomeCliente = demand.getClientId().getName();
    String nomeProfissional = demand.getProfessionalId().getName();
    String titulo = demand.getTitle();
    String codigo = demand.getCode();

    String corpo =
        """
                <p>Olá, <strong>%s</strong>!</p>
                <p>O serviço realizado por <strong>%s</strong> foi concluído.</p>
                <table>
                  <tr><td><strong>Código:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Serviço:</strong></td><td>%s</td></tr>
                </table>
                <p>Que tal avaliar o profissional? Sua opinião ajuda a comunidade ConectaPro!</p>
                <p>Acesse o app para deixar sua avaliação.</p>
                """
            .formatted(nomeCliente, nomeProfissional, codigo, titulo);

    enviar(para, "Serviço concluído, avalie! [%s] - ConectaPro".formatted(codigo), corpo);
  }

  public void notificarDemandaExpirada(Demand demand) {
    String para = demand.getClientId().getEmail();
    String nomeCliente = demand.getClientId().getName();
    String nomeProfissional = demand.getProfessionalId().getName();
    String titulo = demand.getTitle();
    String codigo = demand.getCode();

    String corpo =
        """
                <p>Olá, <strong>%s</strong>!</p>
                <p>Sua solicitação para o profissional <strong>%s</strong> expirou pois ele não respondeu em 1 hora.</p>
                <table>
                  <tr><td><strong>Código:</strong></td><td>%s</td></tr>
                  <tr><td><strong>Serviço:</strong></td><td>%s</td></tr>
                </table>
                <p>Acesse o app para escolher outro profissional e reabrir a solicitação.</p>
                """
            .formatted(nomeCliente, nomeProfissional, codigo, titulo);

    enviar(para, "Solicitação expirada [%s] - ConectaPro".formatted(codigo), corpo);
  }
}
