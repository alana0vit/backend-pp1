package br.com.conectaPro.model.payment;

import br.com.conectaPro.model.demand.Demand;
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
 * Representa uma cobrança gerada para uma demanda. Hoje é processada por um gateway "fake"
 * (nenhum dinheiro é movido de verdade), mas o modelo já reflete o que uma integração real (ex:
 * Mercado Pago) precisaria: valor total, taxa da plataforma, valor líquido do profissional e
 * status do pagamento.
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

  @ManyToOne private Demand demand;

  @Column(nullable = false)
  private Double amount; // valor total cobrado do cliente (= demand.finalValue no momento da criação)

  @Column(nullable = false)
  private Double platformFeeAmount; // fatia da plataforma

  @Column(nullable = false)
  private Double professionalAmount; // fatia do profissional (amount - platformFeeAmount)

  @Column(nullable = false)
  private Double platformFeePercentage; // % usado no cálculo, guardado para histórico/auditoria

  @Enumerated(EnumType.STRING)
  private PaymentStatus status;

  private LocalDateTime paidAt;
}
