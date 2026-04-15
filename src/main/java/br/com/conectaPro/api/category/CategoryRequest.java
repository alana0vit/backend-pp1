package br.com.conectaPro.api.category;

import br.com.conectaPro.model.category.Category;
import jakarta.validation.constraints.*;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {

    @NotBlank
    @Length(max=50)
    private String name;

    @NotBlank()
    @Length(max = 500, message = "A descrição deverá ter no máximo {max} caracteres")
    private String description;

    public Category build() {

        return Category.builder()
                .name(name)
                .description(description)
                .build();
    }
}