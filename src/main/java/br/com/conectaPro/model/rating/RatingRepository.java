package br.com.conectaPro.model.rating;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    @Query("""
        SELECT r
        FROM Rating r
        WHERE r.personEvaluated.id = :userId
    """)
    List<Rating> findRatingsByUser(@Param("userId") Long userId);

    @Query("""
        SELECT AVG(r.points)
        FROM Rating r
        WHERE r.personEvaluated.id = :userId
        AND r.status = br.com.conectaPro.model.rating.EvaluateStatus.COMPLETO
    """)
    Double calculateAverageByUser(@Param("userId") Long userId);

}