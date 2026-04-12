package br.com.conectaPro.api.demand;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.conectaPro.model.demand.Demand;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemandRequest {

    private String code;

    @Length(max = 100, message = "O título deverá ter no máximo {max} caracteres")
    private String title;

    @Length(max = 500, message = "A descrição deverá ter no máximo {max} caracteres")
    private String description;

    private String imgUrl;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate createdAt;

    private Long addressId;

    private Long categoryId;

    private Long clientId;

    private Long professionalId;

    public Demand build() {

        return Demand.builder()
                .code(code)
                .title(title)
                .description(description)
                .imgUrl(imgUrl)
                .createdAt(createdAt)
                .addressId(addressId)
                .categoryId(categoryId)
                .clientId(clientId)
                .professionalId(professionalId)
                .build();
    }
}
