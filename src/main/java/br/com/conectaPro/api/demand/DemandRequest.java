package br.com.conectaPro.api.demand;

import br.com.conectaPro.model.category.Category;
import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.user.AddressUser;
import br.com.conectaPro.model.user.User;
import jakarta.validation.constraints.*;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandRequest {

    @NotBlank
    @Length(max=50)
    private String code;

    @NotBlank()
    @Length(max = 100, message = "O título deverá ter no máximo {max} caracteres")
    private String title;

    @NotBlank()
    @Length(max = 500, message = "A descrição deverá ter no máximo {max} caracteres")
    private String description;

    @URL()
    private String imgUrl;

    @NotNull()
    private AddressUser addressId;

    @NotNull()
    private Category categoryId;

    @NotNull()
    private User clientId;

    @NotNull
    private User professionalId;

    public Demand build() {

        return Demand.builder()
                .code(code)
                .title(title)
                .description(description)
                .imgUrl(imgUrl)
                .addressId(addressId)
                .categoryId(categoryId)
                .clientId(clientId)
                .professionalId(professionalId)
                .build();
    }
}
