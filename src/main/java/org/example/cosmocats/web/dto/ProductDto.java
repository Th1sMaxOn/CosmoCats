package org.example.cosmocats.web.dto;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String productName,
        String description,
        BigDecimal price,
        Long categoryId
) {
}
