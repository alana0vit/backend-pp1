package br.com.conectaPro.model.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);

  Optional<User> findByRecoveryToken(String recoveryToken);

  @Query(
      value =
          """
            SELECT DISTINCT u.*
            FROM Users u

            JOIN Address a
                ON a.user_id_id = u.id

            LEFT JOIN user_category uc
                ON uc.user_id = u.id

            WHERE u.enabled = true
            AND u.user_type = 'PROFESSIONAL'

            -- filtro por nome
            AND (
                :name IS NULL
                OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))
            )

            -- filtro por categoria
            AND (
                :categoryId IS NULL
                OR uc.category_id = :categoryId
            )

            AND (
                :latitude IS NULL
                OR :longitude IS NULL
                OR :radiusKm IS NULL
                OR (
                    a.latitude IS NOT NULL
                    AND a.longitude IS NOT NULL
                    AND (
                        6371 * acos(
                            cos(radians(:latitude))
                            * cos(radians(a.latitude))
                            * cos(radians(a.longitude) - radians(:longitude))
                            + sin(radians(:latitude))
                            * sin(radians(a.latitude))
                        )
                    ) <= :radiusKm
                )
            )
            """,
      nativeQuery = true)
  List<User> searchUsers(
      String name, Long categoryId, Double latitude, Double longitude, Double radiusKm);
}
