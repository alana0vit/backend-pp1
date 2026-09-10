package br.com.conectaPro.api.user;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

  @NotNull(message = "O Nome é de preenchimento obrigatório")
  @NotEmpty(message = "O Nome é de preenchimento obrigatório")
  @Length(max = 100, message = "O Nome deverá ter no máximo {max} caracteres")
  private String name;

  private String enterprise;

  @NotBlank() @Email private String email;

  @NotBlank()
  @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres")
  private String password;

  @JsonFormat(pattern = "dd/MM/yyyy")
  @Past()
  @NotNull()
  private LocalDate birthDate;

  @NotBlank()
  @Pattern(regexp = "^\\d{10,11}$")
  private String phone;

  // rating NÃO é informado pelo cliente — é gerenciado internamente
  // (removido do request para evitar manipulação externa)

  @NotNull() private UserType userType;

  @NotBlank() private String registryId;

  private String photo;

  @NotNull(message = "O endereço é obrigatório")
  @Valid
  private AddressUserRequest address;

  private List<Long> categoriesIds;

  public User build() {
    return User.builder()
        .name(name)
        .enterprise(enterprise)
        .email(email)
        .birthDate(birthDate)
        .phone(phone)
        .userType(userType)
        .registryId(registryId)
        .photo(photo)
        .build();
  }
}
