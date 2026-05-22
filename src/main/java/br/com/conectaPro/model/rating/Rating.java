package br.com.conectaPro.model.rating;

import java.time.LocalDateTime;

import org.hibernate.annotations.SQLRestriction;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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

    @Column()
    private Demand service;

    @Column(unique = true)
    private User evaluatingPerson;

    @Column(unique = true)
    private User personEvaluated;

    @Column()
    private double points;

    @Column(length = 500)
    private String description;

    @Column()
    private LocalDateTime evaluateDate;

    @Column()
    private boolean anonymous;

    @Enumerated(EnumType.STRING)
    private EvaluateStatus status;
}
