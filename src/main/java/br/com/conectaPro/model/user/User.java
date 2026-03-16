package br.com.conectaPro.model.user;

import java.time.LocalDate;

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
@Table(name = "User")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends AudibleEntity {

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password; // Hash

    @Column
    private String cpf; // TODO: definir se vamos especializar cliente-profissional por conta do cnpj

    @Column
    private LocalDate bithDate;

    @Column
    private UserType userType;

    @Column
    private Long addressId; // Address FK

    @Column
    private Boolean active;

}
