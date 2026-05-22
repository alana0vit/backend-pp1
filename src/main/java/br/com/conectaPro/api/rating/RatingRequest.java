package br.com.conectaPro.api.rating;

import java.time.LocalDateTime;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.rating.EvaluateStatus;
import br.com.conectaPro.model.rating.Rating;
import br.com.conectaPro.model.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingRequest {
    private Demand service;

    private User evaluatingPerson;

    private User personEvaluated;

    private double points;

    private String description;

    private LocalDateTime evaluateDate;

    private boolean anonymous;

    private EvaluateStatus status;

    public Rating build() {

        return Rating.builder()
                .service(service)
                .evaluatingPerson(evaluatingPerson)
                .personEvaluated(personEvaluated)
                .points(points)
                .description(description)
                .evaluateDate(evaluateDate)
                .anonymous(anonymous)
                .status(status)
                .build();
    }
}
