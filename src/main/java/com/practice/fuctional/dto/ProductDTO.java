package com.practice.fuctional.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductDTO(
        String id,
        @NotBlank(message = "Name is required")
        String name,
        @NotNull
        @Positive(message = "Price must be positive")
        Double price
) {
}
