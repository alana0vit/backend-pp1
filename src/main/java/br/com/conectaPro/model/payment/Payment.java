package br.com.conectaPro.model.payment;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.subscription.Subscription;
import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

/**
 * Representa uma cobrança gerada para uma demanda (type=DEMANDA) ou para uma assinatura de plano
 * (type=ASSINATURA) — só um dos dois campos (demand/subscription) é preenchido, dependendo do
 * type. É processada por um gateway "fake" (nenhum dinheiro é movido de verdade), mas o modelo já
 * reflete o que uma integração real (ex: Mercado Pago) precisaria: valor total, taxa da
 * plataforma, valor líquido do profissional e status do pagamento.
 */
@Entity
@Table(name = "Payment")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment extends AudibleEntity {

  @Enumerated(EnumType.STRING)
  private PaymentType type;

  @ManyToOne private Demand demand; // preenchido quando type = DEMANDA

  @ManyToOne private Subscription subscription; // preenchido quando type = ASSINATURA

  @Column(nullable = false)
  private Double
      amount; // valor total cobrado (= demand.finalValue ou plan.price no momento da criação)

  @Column(nullable = false)
  private Double platformFeeAmount; // fatia da plataforma (0 para pagamento de assinatura)

  @Column(nullable = false)
  private Double professionalAmount; // fatia do profissional (amount - platformFeeAmount)

  @Column(nullable = false)
  private Double platformFeePercentage; // % usado no cálculo, guardado para histórico/auditoria

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private LocalDateTime paidAt;
}

