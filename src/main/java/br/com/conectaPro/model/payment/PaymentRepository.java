package br.com.conectaPro.model.payment;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

  List<Payment> findByDemandIdOrderByIdDesc(Long demandId);
}
