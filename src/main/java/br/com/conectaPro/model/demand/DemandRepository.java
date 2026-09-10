package br.com.conectaPro.model.demand;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandRepository extends JpaRepository<Demand, Long> {

  // Busca demandas ABERTAS cujo openedAt ultrapassou o limite de tempo informado
  @Query("SELECT d FROM Demand d WHERE d.demandStatus = 'ABERTO' AND d.openedAt <= :limite")
  List<Demand> findDemandasAbertasExpiradas(@Param("limite") LocalDateTime limite);
}
