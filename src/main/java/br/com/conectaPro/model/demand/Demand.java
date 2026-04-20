package br.com.conectaPro.model.demand;

import org.hibernate.annotations.SQLRestriction;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Demand extends AudibleEntity{

    @Column(unique = true)
    private String code;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 500)
    private String description;

    @Column
    private String imgUrl;

    @Column
    private Long addressId;

    @ManyToOne
    private Category categoryId;

    @Column
    private Long clientId;

    @Column
    private Long professionalId;
    
}
