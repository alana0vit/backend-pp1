package br.com.conectaPro.api.demand;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.conectaPro.model.demand.Demand;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

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
    private Long addressId;

    @NotNull()
    private Long categoryId;

    @NotNull()
    private Long clientId;

    @NotNull
    private Long professionalId;

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
