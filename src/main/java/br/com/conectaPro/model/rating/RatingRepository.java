package br.com.conectaPro.model.rating;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

  // Avaliações recebidas (usuário como avaliado)
  List<Rating> findByPersonEvaluatedId(Long userId);

  // Verifica duplicidade (mesmo serviço e mesmo avaliador)
  List<Rating> findByServiceIdAndEvaluatingPersonId(Long serviceId, Long evaluatingPersonId);

  // JOIN FETCH para avaliações feitas pelo usuário (como avaliador)
  @Query(
      """
        SELECT r
        FROM Rating r
        JOIN FETCH r.service
        WHERE r.evaluatingPerson.id = :userId
    """)
  List<Rating> findRatingsByEvaluator(@Param("userId") Long userId);

  // JOIN FETCH para avaliações recebidas pelo usuário (como avaliado)
  @Query(
      """
        SELECT r
        FROM Rating r
        JOIN FETCH r.service
        WHERE r.personEvaluated.id = :userId
    """)
  List<Rating> findRatingsByUser(@Param("userId") Long userId);

  @Query(
      """
        SELECT AVG(r.points)
        FROM Rating r
        WHERE r.personEvaluated.id = :userId
        AND r.status = br.com.conectaPro.model.rating.EvaluateStatus.COMPLETO
    """)
  Double calculateAverageByUser(@Param("userId") Long userId);
}
