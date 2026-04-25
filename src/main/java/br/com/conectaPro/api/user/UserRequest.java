package br.com.conectaPro.api.user;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    @NotNull(message = "O Nome é de preenchimento obrigatório")
    @NotEmpty(message = "O Nome é de preenchimento obrigatório")
    @Length(max = 100, message = "O Nome deverá ter no máximo {max} caracteres")
    private String name;

    @NotBlank()
    @Email
    private String email;

    @NotBlank()
    @Size(min=6, message="A senha deve ter no mínimo 6 caracteres")
    private String password;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @Past()
    @NotNull()
    private LocalDate birthDate;

    @NotBlank()
    @Pattern(
        regexp = "^\\d{10,11}$"
    )
    private String phone;

    @NotNull()
    private UserType userType;

    @NotBlank()
    private String registryId;

    @NotNull(message = "O endereço é obrigatório")
    @Valid // Para garantir que as validações do AddressUserRequest sejam executadas
    private AddressUserRequest address;

    // Não mapeamos as categorias aqui, pois precisamos do banco para isso
    // Faremos isso no service
    private List<Long> categoriesIds; 

    public User build() {

        return User.builder()
                .name(name)
                .email(email)
                .password(password)
                .birthDate(birthDate)
                .phone(phone)
                .userType(userType)
                .registryId(registryId)
                // Não mapeamos o endereço aqui, faremos no Service
                // .adresses(adresses)
                .build();
    }

}
