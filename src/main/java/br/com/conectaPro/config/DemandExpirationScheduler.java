package br.com.conectaPro.config;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandRepository;
import br.com.conectaPro.model.demand.DemandStatus;
import br.com.conectaPro.security.EmailService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DemandExpirationScheduler {

  private final DemandRepository demandRepository;
  private final EmailService emailService;

  /**
   * Roda a cada 5 minutos e expira demandas abertas há mais de 1 hora sem resposta. fixedDelay = 5
   * * 60 * 1000 ms
   */
  @Scheduled(fixedDelay = 5 * 60 * 1000)
  @Transactional
  public void expirarDemandasAbertas() {
    LocalDateTime limiteDeResposta = LocalDateTime.now().minusHours(1);

    List<Demand> expiradas = demandRepository.findDemandasAbertasExpiradas(limiteDeResposta);

    if (expiradas.isEmpty()) return;

    System.out.println("[CRON] Expirando " + expiradas.size() + " demanda(s) sem resposta.");

    for (Demand demand : expiradas) {
      demand.setDemandStatus(DemandStatus.EXPIRADO);
      demandRepository.save(demand);

      try {
        emailService.notificarDemandaExpirada(demand);
      } catch (Exception e) {
        System.err.println(
            "[CRON] Erro ao notificar expiração da demanda "
                + demand.getCode()
                + ": "
                + e.getMessage());
      }
    }
  }
}
