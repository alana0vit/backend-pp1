package br.com.conectaPro.api.user;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String name;

    private String email;

    private String password;

    private String cpf_cnpj; // João, não é melhor colocar cada um como atributo em cliente/profissional?

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
