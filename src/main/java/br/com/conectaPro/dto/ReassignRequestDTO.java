package br.com.conectaPro.dto;

import jakarta.validation.constraints.NotBlank;

public record ReassignRequestDTO(
        @NotBlank Long professionalId) {

}
