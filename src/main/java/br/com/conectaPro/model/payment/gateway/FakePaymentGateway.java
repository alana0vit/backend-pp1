package br.com.conectaPro.model.payment.gateway;

import br.com.conectaPro.model.payment.Payment;
import org.springframework.stereotype.Component;

/**
 * Implementação simulada do {@link PaymentGateway}. Não integra com nenhum provedor externo:
 * apenas devolve a URL de uma "tela de checkout" servida pela própria API
 * (GET /api/payments/{id}/fake-checkout), onde o pagamento é aprovado/recusado manualmente
 * chamando os endpoints /aprovar ou /recusar. Serve para desenvolver e testar todo o fluxo de
 * pagamento (e, em breve, de assinatura) sem depender de credenciais de um gateway real.
 */
@Component
public class FakePaymentGateway implements PaymentGateway {

  @Override
  public String criarCheckout(Payment payment) {
    return "/api/payments/" + payment.getId() + "/fake-checkout";
  }
}
