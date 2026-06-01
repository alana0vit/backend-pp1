package br.com.conectaPro.model.rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    @Query("""
                SELECT AVG(r.points)
                FROM Rating r
                WHERE r.personEvaluated.id = :userId
                AND r.status = 'COMPLETO'
            """)
    Double calculateAverageByUser(Long userId);

}
