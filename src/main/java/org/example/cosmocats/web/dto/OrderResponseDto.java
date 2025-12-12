package org.example.cosmocats.web.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDto(
        Long id,
        String orderNumber,
        String status,
        Instant createdAt,
        String customerEmail,
        String customerFullName,
        BigDecimal totalAmount,
        List<OrderLineDto> lines
) {
}