package br.com.conectaPro.api.rating;

import br.com.conectaPro.model.rating.EvaluateStatus;
import br.com.conectaPro.model.rating.Rating;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
  private Long service;

  private Long evaluatingPerson;

  private Long personEvaluated;

  private Integer points;

  private String description;

  private boolean anonymous;

  private EvaluateStatus status;

  public Rating build() {

    return Rating.builder()
        .points(points)
        .description(description)
        .anonymous(anonymous)
        .status(status)
        .build();
  }
}
