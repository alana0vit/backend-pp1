package br.com.conectaPro.config;

import br.com.conectaPro.model.subscription.SubscriptionPlan;
import br.com.conectaPro.model.subscription.SubscriptionPlanRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Cadastra/atualiza os planos de assinatura padrão a cada início da aplicação. É um "upsert": se
 * o plano ainda não existe, cria; se já existe (pelo nome), atualiza preço/taxa/prioridade pros
 * valores daqui. Isso evita que um valor antigo fique preso no banco depois de uma mudança aqui
 * no código — comum durante o desenvolvimento, enquanto os planos ainda estão sendo ajustados.
 */
@Component
public class SubscriptionPlanSeeder implements CommandLineRunner {

  private final SubscriptionPlanRepository planRepository;

  public SubscriptionPlanSeeder(SubscriptionPlanRepository planRepository) {
    this.planRepository = planRepository;
  }

  @Override
  public void run(String... args) {
    seedPlans();
  }

  private void seedPlans() {
    List<SubscriptionPlan> plans =
        List.of(
            SubscriptionPlan.builder()
                .name("Bronze")
                .description("Selo de verificado e prioridade básica na busca")
                .price(19.90)
                .durationDays(30)
                .platformFeePercentage(0.10) // taxa padrão é 0.15
                .priorityWeight(1)
                .badgeLabel("Verificado")
                .build(),
            SubscriptionPlan.builder()
                .name("Prata")
                .description("Mais prioridade na busca e taxa reduzida")
                .price(39.90)
                .durationDays(30)
                .platformFeePercentage(0.05)
                .priorityWeight(2)
                .badgeLabel("Verificado Prata")
                .build(),
            SubscriptionPlan.builder()
                .name("Ouro")
                .description("Prioridade máxima na busca e taxa de plataforma zerada")
                .price(69.90)
                .durationDays(30)
                .platformFeePercentage(0.0)
                .priorityWeight(3)
                .badgeLabel("Verificado Ouro")
                .build());

    for (SubscriptionPlan seedPlan : plans) {
      SubscriptionPlan plan =
          planRepository
              .findByName(seedPlan.getName())
              .map(
                  existente -> {
                    existente.setDescription(seedPlan.getDescription());
                    existente.setPrice(seedPlan.getPrice());
                    existente.setDurationDays(seedPlan.getDurationDays());
                    existente.setPlatformFeePercentage(seedPlan.getPlatformFeePercentage());
                    existente.setPriorityWeight(seedPlan.getPriorityWeight());
                    existente.setBadgeLabel(seedPlan.getBadgeLabel());
                    return existente;
                  })
              .orElse(seedPlan);

      plan.setEnabled(true);
      planRepository.save(plan);
    }

    System.out.println("Planos de assinatura sincronizados: Bronze, Prata, Ouro.");
  }
}
