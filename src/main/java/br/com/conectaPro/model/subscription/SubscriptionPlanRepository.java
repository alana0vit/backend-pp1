package br.com.conectaPro.model.subscription;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {

  Optional<SubscriptionPlan> findByName(String name);

  boolean existsByName(String name);
}
