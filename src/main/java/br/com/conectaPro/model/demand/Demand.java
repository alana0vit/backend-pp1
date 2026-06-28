package br.com.conectaPro.model.demand;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Demand")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Demand extends AudibleEntity {

    @Column(unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "demand_images", joinColumns = @JoinColumn(name = "demand_id"))
    @Column(name = "img_url")
    private List<String> imgUrl;

    @Column
    private Double suggestedValue;

    @Column
    private LocalDate suggestedDate;

    @ManyToOne
    private AddressUser addressId;

    @ManyToOne
    private Category categoryId;

    @ManyToOne
    private User clientId;

    @ManyToOne
    private User professionalId;

    @Enumerated(EnumType.STRING)
    private DemandStatus demandStatus;

}