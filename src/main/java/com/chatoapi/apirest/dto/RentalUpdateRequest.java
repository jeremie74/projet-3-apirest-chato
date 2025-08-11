package com.chatoapi.apirest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RentalUpdateRequest(
        @NotBlank String name,
        @NotBlank String picture,
        @NotNull @Positive Integer surface,
        @NotNull @Positive BigDecimal price,
        @NotBlank @Size(max = 2000) String description) {
}
