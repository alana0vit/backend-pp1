package br.com.conectaPro.dto;

import br.com.conectaPro.model.user.UserType;

public record LoginResponseDTO(
        String tooken,
        Long id,
        String name,
        UserType userType) {
}
