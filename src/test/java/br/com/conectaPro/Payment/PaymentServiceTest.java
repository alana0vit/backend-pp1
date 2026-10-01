package br.com.conectaPro.Payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandService;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.model.payment.Payment;
import br.com.conectaPro.model.payment.PaymentRepository;
import br.com.conectaPro.model.payment.PaymentService;
import br.com.conectaPro.model.payment.PaymentStatus;
import br.com.conectaPro.model.payment.PaymentType;
import br.com.conectaPro.model.payment.gateway.PaymentGateway;
import br.com.conectaPro.model.subscription.Subscription;
import br.com.conectaPro.model.subscription.SubscriptionPlan;
import br.com.conectaPro.model.subscription.SubscriptionService;
import br.com.conectaPro.model.subscription.SubscriptionStatus;
import br.com.conectaPro.model.user.User;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

  @Mock private PaymentRepository paymentRepository;
  @Mock private DemandService demandService;
  @Mock private SubscriptionService subscriptionService;
  @Mock private PaymentGateway paymentGateway;
  @InjectMocks private PaymentService paymentService;

  @BeforeEach
  void setUp() {
    // @Value não é resolvido pelo Mockito puro, então setamos manualmente
    ReflectionTestUtils.setField(paymentService, "defaultPlatformFeePercentage", 0.15);
  }

  @Test
  @DisplayName("Deve criar cobrança de demanda calculando corretamente a taxa da plataforma")
  void deveCriarCheckoutDemandaComSucesso() {
    Long demandId = 1L;
    Long professionalId = 10L;

    User professional = new User();
    professional.setId(professionalId);

    Demand demanda = new Demand();
    demanda.setId(demandId);
    demanda.setDemandStatus(DemandStatus.AGUARDANDO_PAGAMENTO);
    demanda.setFinalValue(100.0);
    demanda.setProfessionalId(professional);

    when(demandService.getById(demandId)).thenReturn(demanda);
    when(subscriptionService.getPlatformFeePercentage(professionalId, 0.15)).thenReturn(0.15);
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment payment = paymentService.criarCheckoutDemanda(demandId);

    assertEquals(PaymentType.DEMANDA, payment.getType());
    assertEquals(100.0, payment.getAmount());
    assertEquals(15.0, payment.getPlatformFeeAmount());
    assertEquals(85.0, payment.getProfessionalAmount());
    assertEquals(PaymentStatus.PENDENTE, payment.getStatus());
  }

  @Test
  @DisplayName("Deve usar a taxa reduzida do plano do profissional, quando houver")
  void deveUsarTaxaReduzidaDoPlano() {
    Long demandId = 1L;
    Long professionalId = 10L;

    User professional = new User();
    professional.setId(professionalId);

    Demand demanda = new Demand();
    demanda.setId(demandId);
    demanda.setDemandStatus(DemandStatus.AGUARDANDO_PAGAMENTO);
    demanda.setFinalValue(100.0);
    demanda.setProfessionalId(professional);

    when(demandService.getById(demandId)).thenReturn(demanda);
    when(subscriptionService.getPlatformFeePercentage(professionalId, 0.15)).thenReturn(0.07);
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment payment = paymentService.criarCheckoutDemanda(demandId);

    assertEquals(0.07, payment.getPlatformFeePercentage());
    assertEquals(7.0, payment.getPlatformFeeAmount());
    assertEquals(93.0, payment.getProfessionalAmount());
  }

  @Test
  @DisplayName("Não deve criar cobrança se a demanda não estiver aguardando pagamento")
  void erroAoCriarCheckoutComStatusInvalido() {
    Long demandId = 1L;

    Demand demanda = new Demand();
    demanda.setId(demandId);
    demanda.setDemandStatus(DemandStatus.ABERTO);

    when(demandService.getById(demandId)).thenReturn(demanda);

    IllegalStateException exception =
        assertThrows(
            IllegalStateException.class, () -> paymentService.criarCheckoutDemanda(demandId));

    assertEquals(
        "Só é possível gerar cobrança para uma demanda aguardando pagamento.",
        exception.getMessage());
  }

  @Test
  @DisplayName("Deve criar cobrança de assinatura sem taxa de plataforma")
  void deveCriarCheckoutAssinaturaComSucesso() {
    SubscriptionPlan plano = SubscriptionPlan.builder().name("Ouro").price(69.90).build();

    Subscription subscription =
        Subscription.builder().plan(plano).status(SubscriptionStatus.PENDENTE).build();
    subscription.setId(5L);

    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment payment = paymentService.criarCheckoutAssinatura(subscription);

    assertEquals(PaymentType.ASSINATURA, payment.getType());
    assertEquals(69.90, payment.getAmount());
    assertEquals(0.0, payment.getPlatformFeeAmount());
    assertEquals(69.90, payment.getProfessionalAmount());
  }

  @Test
  @DisplayName("Deve aprovar pagamento de demanda e confirmar a demanda")
  void deveAprovarPagamentoDeDemanda() {
    Long paymentId = 1L;
    Long demandId = 2L;

    Demand demanda = new Demand();
    demanda.setId(demandId);

    Payment payment = new Payment();
    payment.setId(paymentId);
    payment.setType(PaymentType.DEMANDA);
    payment.setDemand(demanda);
    payment.setStatus(PaymentStatus.PENDENTE);

    when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.of(payment));
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment aprovado = paymentService.aprovar(paymentId);

    assertEquals(PaymentStatus.APROVADO, aprovado.getStatus());
    verify(demandService).confirmarPagamento(demandId);
  }

  @Test
  @DisplayName("Deve aprovar pagamento de assinatura e ativar a assinatura")
  void deveAprovarPagamentoDeAssinatura() {
    Long paymentId = 1L;
    Long subscriptionId = 3L;

    Subscription subscription = new Subscription();
    subscription.setId(subscriptionId);

    Payment payment = new Payment();
    payment.setId(paymentId);
    payment.setType(PaymentType.ASSINATURA);
    payment.setSubscription(subscription);
    payment.setStatus(PaymentStatus.PENDENTE);

    when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.of(payment));
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment aprovado = paymentService.aprovar(paymentId);

    assertEquals(PaymentStatus.APROVADO, aprovado.getStatus());
    verify(subscriptionService).ativar(subscriptionId);
    verify(demandService, org.mockito.Mockito.never()).confirmarPagamento(anyLong());
  }

  @Test
  @DisplayName("Não deve aprovar um pagamento que não está PENDENTE")
  void erroAoAprovarPagamentoJaProcessado() {
    Long paymentId = 1L;

    Payment payment = new Payment();
    payment.setId(paymentId);
    payment.setStatus(PaymentStatus.APROVADO);

    when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.of(payment));

    IllegalStateException exception =
        assertThrows(IllegalStateException.class, () -> paymentService.aprovar(paymentId));

    assertEquals("Só é possível aprovar um pagamento PENDENTE.", exception.getMessage());
  }

  @Test
  @DisplayName("Deve recusar pagamento sem alterar a demanda")
  void deveRecusarPagamento() {
    Long paymentId = 1L;

    Payment payment = new Payment();
    payment.setId(paymentId);
    payment.setStatus(PaymentStatus.PENDENTE);

    when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.of(payment));
    when(paymentRepository.save(any(Payment.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Payment recusado = paymentService.recusar(paymentId);

    assertEquals(PaymentStatus.RECUSADO, recusado.getStatus());
  }

  @Test
  @DisplayName("Deve lançar exceção ao buscar pagamento inexistente")
  void erroAoBuscarPagamentoInexistente() {
    Long paymentId = 99L;

    when(paymentRepository.findById(paymentId)).thenReturn(java.util.Optional.empty());

    NoSuchElementException exception =
        assertThrows(NoSuchElementException.class, () -> paymentService.getById(paymentId));

    assertEquals("Pagamento não encontrado com ID: " + paymentId, exception.getMessage());
  }
}
