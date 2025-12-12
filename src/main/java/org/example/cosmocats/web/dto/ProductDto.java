package org.example.cosmocats.web.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductDto(
        Long id,

        @NotBlank(message = "Product name is required")
        String productName,

        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @NotNull(message = "Category ID is required")
        Long categoryId
) {
}