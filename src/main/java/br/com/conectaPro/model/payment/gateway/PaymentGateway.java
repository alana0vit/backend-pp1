package br.com.conectaPro.model.payment.gateway;

import br.com.conectaPro.model.payment.Payment;

/**
 * Abstração do provedor de pagamento. Hoje só existe o {@link FakePaymentGateway} (simulado, sem
 * movimentação real de dinheiro). Quando o time decidir integrar um provedor de verdade (ex:
 * Mercado Pago), basta implementar essa interface e trocar o bean ativo — o resto do sistema
 * (PaymentService, PaymentController, fluxo da demanda) não precisa mudar.
 */
public interface PaymentGateway {

  /**
   * Gera a cobrança no provedor e retorna uma URL/referência de checkout para o cliente
   * completar o pagamento. No gateway fake, essa URL aponta para um endpoint interno de
   * simulação; num gateway real, seria o link de checkout do provedor.
   */
  String criarCheckout(Payment payment);
}
