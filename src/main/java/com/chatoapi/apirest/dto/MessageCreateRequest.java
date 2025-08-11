package com.chatoapi.apirest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MessageCreateRequest(
        @JsonProperty("user_id") @NotNull Long userId,
        @JsonProperty("rental_id") @NotNull Long rentalId,
        @NotBlank @Size(max = 2000) String message) {
}
