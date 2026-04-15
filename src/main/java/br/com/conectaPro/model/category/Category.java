package br.com.conectaPro.model.category;

import org.hibernate.annotations.SQLRestriction;

import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Category")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends AudibleEntity{

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;
    // João, achas melhor deixar na descrição os serviços inclusos, ou crio uma nova coluna/atributo como um array/list para guardar todos os serviços inclusos?
    
}