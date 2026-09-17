package br.com.conectaPro.model.payment;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.model.payment.gateway.PaymentGateway;
import jakarta.transaction.Transactional;

@Service
public class PaymentService {

  @Autowired private PaymentRepository repository;

  @Autowired private DemandService demandService;

  @Autowired private PaymentGateway paymentGateway;

  @Value("${payment.platform.fee.percentage}")
  private Double platformFeePercentage;

  /**
   * Cria uma nova cobrança para a demanda (deve estar em AGUARDANDO_PAGAMENTO) e retorna o
   * Payment já com a URL de checkout preenchida via {@link #getCheckoutUrl}.
   */
  @Transactional
  public Payment criarCheckout(@NonNull Long demandId) {
    Demand demand = demandService.getById(demandId);

    if (demand.getDemandStatus() != DemandStatus.AGUARDANDO_PAGAMENTO) {
      throw new IllegalStateException(
          "Só é possível gerar cobrança para uma demanda aguardando pagamento.");
    }

    if (demand.getFinalValue() == null) {
      throw new IllegalStateException("A demanda não possui um valor final definido.");
    }

    double amount = demand.getFinalValue();
    double platformFeeAmount = arredondar(amount * platformFeePercentage);
    double professionalAmount = arredondar(amount - platformFeeAmount);

    Payment payment =
        Payment.builder()
            .demand(demand)
            .amount(amount)
            .platformFeeAmount(platformFeeAmount)
            .professionalAmount(professionalAmount)
            .platformFeePercentage(platformFeePercentage)
            .status(PaymentStatus.PENDENTE)
            .build();
    payment.setEnabled(Boolean.TRUE);

    return repository.save(payment);
  }

  public String getCheckoutUrl(Payment payment) {
    return paymentGateway.criarCheckout(payment);
  }

  public Payment getById(@NonNull Long id) {
    return repository
        .findById(id)
        .orElseThrow(() -> new NoSuchElementException("Pagamento não encontrado com ID: " + id));
  }

  /**
   * Aprova o pagamento (chamado pela tela/endpoint de simulação hoje; seria chamado pelo webhook
   * de um gateway real no futuro) e avança a demanda para AGUARDANDO.
   */
  @Transactional
  public Payment aprovar(@NonNull Long paymentId) {
    Payment payment = getById(paymentId);

    if (payment.getStatus() != PaymentStatus.PENDENTE) {
      throw new IllegalStateException("Só é possível aprovar um pagamento PENDENTE.");
    }

    payment.setStatus(PaymentStatus.APROVADO);
    payment.setPaidAt(LocalDateTime.now());
    Payment saved = repository.save(payment);

    demandService.confirmarPagamento(payment.getDemand().getId());

    return saved;
  }

  @Transactional
  public Payment recusar(@NonNull Long paymentId) {
    Payment payment = getById(paymentId);

    if (payment.getStatus() != PaymentStatus.PENDENTE) {
      throw new IllegalStateException("Só é possível recusar um pagamento PENDENTE.");
    }

    payment.setStatus(PaymentStatus.RECUSADO);
    // A demanda permanece em AGUARDANDO_PAGAMENTO; o cliente pode gerar uma nova cobrança.
    return repository.save(payment);
  }

  private double arredondar(double valor) {
    return Math.round(valor * 100.0) / 100.0;
  }
}