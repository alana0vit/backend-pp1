package br.com.conectaPro.model.subscription;

import br.com.conectaPro.model.user.User;
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
 * Uma contratação de um {@link SubscriptionPlan} por um profissional. Uma nova Subscription é
 * criada a cada compra/renovação (não é reaproveitada); o histórico de ciclos fica todo aqui.
 */
@Entity
@Table(name = "Subscription")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Subscription extends AudibleEntity {

  @ManyToOne private User professional;

  @ManyToOne private SubscriptionPlan plan;

  @Enumerated(EnumType.STRING)
  private SubscriptionStatus status;

  @Column private LocalDateTime startedAt;

  @Column private LocalDateTime expiresAt;
}
