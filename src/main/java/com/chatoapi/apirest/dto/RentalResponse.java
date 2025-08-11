package com.chatoapi.apirest.dto;

public record RentalResponse(
        Long id,
        String name,
        Integer surface,
        String picture,
        String description,
        String created_at,
        String updated_at,
        Long owner_id,
        String price // string pour éviter les pièges BigDecimal en JSON
) {
}
