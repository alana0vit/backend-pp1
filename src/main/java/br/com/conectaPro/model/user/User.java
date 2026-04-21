package br.com.conectaPro.model.user;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.util.entity.AudibleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Users")
@SQLRestriction("enabled = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends AudibleEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true)
    private String email;

    @Column
    private String password; // Hash

    @Column
    private LocalDate birthDate;

    @Column
    private String phone;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @Column
    private String registryId;

    @OneToMany(mappedBy = "userId", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<AddressUser> adresses;

    @ManyToMany
    @JoinTable(name = "user_category", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<Category> categories;

}