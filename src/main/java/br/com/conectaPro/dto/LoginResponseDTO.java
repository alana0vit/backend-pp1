package br.com.conectaPro.dto;

import br.com.conectaPro.model.user.UserType;

public record LoginResponseDTO(
        String token,
        Long id,
        String name,
        UserType userType) {
}
