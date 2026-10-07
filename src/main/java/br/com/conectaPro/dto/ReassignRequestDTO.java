package br.com.conectaPro.dto;

import jakarta.validation.constraints.NotNull;

public record ReassignRequestDTO(@NotNull Long professionalId) {}
