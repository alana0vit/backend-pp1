package br.com.conectaPro.model.user;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.util.entity.AudibleEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "Users")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends AudibleEntity {

  @Column(nullable = false, length = 100)
  private String name;

  @Column() private String enterprise;

  @Column(unique = true)
  private String email;

  @JsonIgnore
  @Column
  private String password; // Hash — nunca deve sair em resposta de API

  @Column private LocalDate birthDate;

  @Column private String phone;

  @Column private Double rating;

  @Enumerated(EnumType.STRING)
  private UserType userType;

  @Column private String registryId;

  @Column private String photo;

  @JsonIgnore
  @Column
  private String recoveryToken; // usado só no fluxo de "esqueci minha senha", nunca em resposta

  @JsonIgnore
  @Column
  private LocalDateTime recoveryTokenExpiration;

  @OneToMany(mappedBy = "userId", orphanRemoval = true, fetch = FetchType.EAGER)
  private List<AddressUser> adresses;

  @ManyToMany
  @JoinTable(
      name = "user_category",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private List<Category> categories;

  // Campos denormalizados a partir da assinatura ativa (ver model.subscription).
  // Mantidos aqui (em vez de um JOIN na busca) para não mexer na query nativa de
  // busca por raio/categoria, que usa SELECT DISTINCT (JOIN + ORDER BY por coluna
  // fora do SELECT quebraria isso). Atualizados pelo SubscriptionService sempre que
  // uma assinatura é ativada/expira/cancelada.
  // Sem nullable=false de propósito: evita quebrar o ALTER TABLE em bancos que já
  // têm linhas (o default abaixo só vale para objetos novos criados na JVM).
  @Builder.Default
  @Column
  private Boolean verified = false;

  // Usado só para ordenar a busca (maior = aparece antes). null/0 = sem plano.
  @Builder.Default
  @Column
  private Integer priorityWeight = 0;

  // Nome do plano ativo, para exibir o selo sem precisar buscar a assinatura à parte.
  @Column private String activePlanName;
}
