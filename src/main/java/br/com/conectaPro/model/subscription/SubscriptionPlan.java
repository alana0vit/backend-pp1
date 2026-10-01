package br.com.conectaPro.model.subscription;

import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Catálogo de planos de assinatura (verificação) que um profissional pode contratar. Cada nível
 * define seu próprio preço, taxa de plataforma, peso de prioridade na busca e rótulo do selo. Os
 * planos em si são cadastro (ver {@link SubscriptionPlanSeeder}); quem liga um profissional a um
 * plano é a entidade {@link Subscription}.
 */
@Entity
@Table(name = "SubscriptionPlan")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionPlan extends AudibleEntity {

  @Column(nullable = false, unique = true, length = 50)
  private String name; // ex: "Bronze", "Prata", "Ouro"

  @Column private String description;

  @Column(nullable = false)
  private Double price;

  @Column(nullable = false)
  private Integer durationDays; // duração de cada ciclo (ex: 30 = mensal)

  @Column(nullable = false)
  private Double platformFeePercentage; // taxa cobrada do profissional nas demandas pagas

  @Column(nullable = false)
  private Integer priorityWeight; // maior = aparece antes na busca

  @Column(nullable = false)
  private String badgeLabel; // texto do selo mostrado no perfil (ex: "Ouro Verificado")
}
