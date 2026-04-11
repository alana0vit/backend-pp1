package br.com.conectaPro.api.user;

import java.time.LocalDate;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.br.CPF;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotNull(message = "O Nome é de preenchimento obrigatório")
    @NotEmpty(message = "O Nome é de preenchimento obrigatório")
    @Length(max = 100, message = "O Nome deverá ter no máximo {max} caracteres")

    private String name;

    @Email
    private String email;

    private String password;

    private String cpf_cnpj;
    // João, não é melhor colocar cada um como atributo em cliente/profissional?
    // ou fazer um enum com userType?
    // até para usar o @CPF @CNPJ

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    private UserType userType;

    private Long addressId;

    private Boolean active;

    public User build() {

        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .cpf_cnpj(cpf_cnpj)
                .birthDate(birthDate)
                .userType(userType)
                .addressId(addressId)
                .active(active)
                .build();
    }

}
