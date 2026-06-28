package br.com.conectaPro.api.demand;

import java.time.LocalDate;
import java.util.ArrayList;

import org.hibernate.validator.constraints.Length;

import br.com.conectaPro.model.demand.Demand;
import br.com.conectaPro.model.demand.DemandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandRequest {

    @Length(max = 50)
    private String code;

    @Length(max = 100, message = "O título deverá ter no máximo {max} caracteres")
    private String title;

    @Length(max = 500, message = "A descrição deverá ter no máximo {max} caracteres")
    private String description;

    private Double suggestedValue;

    private LocalDate suggestedDate;

    private Long addressId;

    private Long categoryId;

    private Long clientId;

    private DemandStatus demandStatus;

    private Long professionalId;

    public Demand build() {
        return Demand.builder()
                .code(code)
                .title(title)
                .description(description)
                .suggestedValue(suggestedValue)
                .suggestedDate(suggestedDate)
                .demandStatus(demandStatus)
                .imgUrl(new ArrayList<>())
                .build();
    }
}