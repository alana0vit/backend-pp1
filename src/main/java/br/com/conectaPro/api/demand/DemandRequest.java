package br.com.conectaPro.api.demand;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull()
    private DemandStatus demandStatus;

    @NotNull
    private Long professionalId;

    public Demand build() {

        return Demand.builder()
                .code(code)
                .title(title)
                .description(description)
                .imgUrl(imgUrl)
                .demandStatus(demandStatus)
                .build();
    }
}