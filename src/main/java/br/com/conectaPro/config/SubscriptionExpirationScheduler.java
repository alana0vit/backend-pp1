package br.com.conectaPro.config;

import br.com.conectaPro.model.subscription.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionExpirationScheduler {

  private final SubscriptionService subscriptionService;

  /**
   * Roda a cada 30 minutos e expira assinaturas vencidas, removendo os benefícios do profissional.
   */
  @Scheduled(fixedDelay = 30 * 60 * 1000)
  public void expirarAssinaturasVencidas() {
    subscriptionService.expirarVencidas();
  }
}
