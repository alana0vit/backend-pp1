package br.com.conectaPro.dto;

import java.util.List;

import br.com.conectaPro.dto.UserResponseDTO.CategoryBasicDTO;

public record SearchDTO (
    Long id,
    String name,
    Double rating,
    Double distanceKm,
    List<CategoryBasicDTO> categories
) {}