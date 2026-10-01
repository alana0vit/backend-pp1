package br.com.conectaPro.api.payment;

import br.com.conectaPro.model.payment.Payment;
import br.com.conectaPro.model.payment.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
@Tag(
    name = "Payments",
    description =
        "Cobrança/pagamento das demandas. Hoje usa um gateway simulado (nenhum dinheiro real é"
            + " movimentado); a interface foi desenhada para trocar por um provedor real (ex:"
            + " Mercado Pago) sem mudar o resto do sistema.")
public class PaymentController {

  @Autowired private PaymentService paymentService;

  @Operation(summary = "Gera uma cobrança para a demanda (deve estar AGUARDANDO_PAGAMENTO)")
  @PostMapping("/demand/{demandId}/checkout")
  public ResponseEntity<?> criarCheckoutDemanda(@PathVariable Long demandId) {
    try {
      Payment payment = paymentService.criarCheckoutDemanda(demandId);
      String checkoutUrl = paymentService.getCheckoutUrl(payment);

      return ResponseEntity.ok(
          Map.of(
              "payment", payment,
              "checkoutUrl", checkoutUrl));
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @Operation(summary = "Consulta um pagamento")
  @GetMapping("/{id}")
  public ResponseEntity<?> getById(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(paymentService.getById(id));
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @Operation(
      summary = "[SIMULADO] Tela de checkout fake para testar o fluxo sem um gateway real",
      description =
          "Só existe enquanto usamos o FakePaymentGateway. Some quando trocarmos por um provedor"
              + " de verdade (o cliente seria redirecionado pro checkout do provedor).")
  @GetMapping(value = "/{id}/fake-checkout", produces = MediaType.TEXT_HTML_VALUE)
  public ResponseEntity<String> fakeCheckout(@PathVariable Long id) {
    Payment payment;
    try {
      payment = paymentService.getById(id);
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pagamento não encontrado.");
    }

    String html =
        """
        <!DOCTYPE html>
        <html lang="pt-br">
        <head>
          <meta charset="UTF-8">
          <title>Checkout simulado - ConectaPro</title>
          <style>
            body { font-family: sans-serif; max-width: 420px; margin: 60px auto; text-align: center; }
            .badge { background: #fef3c7; color: #92400e; padding: 4px 10px; border-radius: 6px; font-size: 12px; }
            .valor { font-size: 32px; font-weight: bold; margin: 20px 0; }
            button { padding: 12px 20px; margin: 8px; border: none; border-radius: 8px; font-size: 16px; cursor: pointer; }
            .aprovar { background: #16a34a; color: white; }
            .recusar { background: #dc2626; color: white; }
          </style>
        </head>
        <body>
          <span class="badge">MODO SIMULADO — nenhum dinheiro real será cobrado</span>
          <h2>Pagamento #%d</h2>
          <div class="valor">R$ %.2f</div>
          <p>Status atual: <strong>%s</strong></p>
          <button class="aprovar" onclick="enviar('aprovar')">Aprovar pagamento</button>
          <button class="recusar" onclick="enviar('recusar')">Recusar pagamento</button>
          <p id="resultado"></p>
          <script>
            async function enviar(acao) {
              const resp = await fetch(`/api/payments/%d/${acao}`, { method: 'POST' });
              const texto = await resp.text();
              document.getElementById('resultado').innerText =
                resp.ok ? 'Feito! Pode fechar esta janela.' : ('Erro: ' + texto);
            }
          </script>
        </body>
        </html>
        """
            .formatted(payment.getId(), payment.getAmount(), payment.getStatus(), payment.getId());

    return ResponseEntity.ok(html);
  }

  @Operation(summary = "[SIMULADO] Aprova o pagamento manualmente")
  @PostMapping("/{id}/aprovar")
  public ResponseEntity<?> aprovar(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(paymentService.aprovar(id));
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @Operation(summary = "[SIMULADO] Recusa o pagamento manualmente")
  @PostMapping("/{id}/recusar")
  public ResponseEntity<?> recusar(@PathVariable Long id) {
    try {
      return ResponseEntity.ok(paymentService.recusar(id));
    } catch (NoSuchElementException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }
}
