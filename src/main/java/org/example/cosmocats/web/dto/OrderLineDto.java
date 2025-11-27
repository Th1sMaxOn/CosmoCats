package org.example.cosmocats.web.dto;

import java.math.BigDecimal;

public record OrderLineDto(
        Long productId,
        Integer quantity,
        BigDecimal priceAtPurchase
) {
}
