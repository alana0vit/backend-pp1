package br.com.conectaPro.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import br.com.conectaPro.model.user.User;
import br.com.conectaPro.model.user.UserType;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        LocalDate birthDate,
        UserType userType,
        String registryId,
        List<CategoryBasicDTO> categories) {
    // Record auxiliar para devolver apenas o necessário da Categoria
    public record CategoryBasicDTO(Long id, String name) {
    }

    // Mapper estático para facilitar a conversão
    public static UserResponseDTO fromEntity(User user) {
        List<CategoryBasicDTO> categoryDTOs = user.getCategories() == null ? List.of()
                : user.getCategories().stream()
                        .map(c -> new CategoryBasicDTO(c.getId(), c.getName()))
                        .collect(Collectors.toList());

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate(),
                user.getUserType(),
                user.getRegistryId(),
                categoryDTOs);
    }
}