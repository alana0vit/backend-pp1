package br.com.conectaPro.api.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import br.com.conectaPro.model.user.AddressUser;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressUserRequest {

    @NotBlank(message = "A rua não é opcional")
    private String street;

    private String number;

    @NotBlank(message = "O bairro não é opcional")
    private String neighborhood;

    @NotBlank(message = "A cidade não é opcional")
    private String city;

    @NotBlank(message = "O Estado não é opcional")
    @Size(min = 2, max = 2)
    private String state;

    @NotBlank(message = "O CEP é obrigatório")
    @Pattern(
        regexp = "\\d{5}-?\\d{3}"
    )
    private String zipCode;

    private Double latitude;

    private Double longitude;

    private String supplement;

    public AddressUser build() {

        return AddressUser.builder()
                .street(street)
                .number(number)
                .neighborhood(neighborhood)
                .city(city)
                .state(state)
                .zipCode(zipCode)
                .latitude(latitude)
                .longitude(longitude)
                .supplement(supplement)
                .build();
    }
}