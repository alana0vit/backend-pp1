package br.com.conectaPro.model.rating;

import org.hibernate.annotations.SQLRestriction;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Rating")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Rating extends AudibleEntity {

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Demand service;

    @ManyToOne
    @JoinColumn(name = "evaluating_person_id")
    private User evaluatingPerson;

    @ManyToOne
    @JoinColumn(name = "person_evaluated_id")
    private User personEvaluated;

    @Column
    private Integer points;

    @Column(length = 500)
    private String description;

    @Column
    private Boolean anonymous;

    @Enumerated(EnumType.STRING)
    private EvaluateStatus status;
}