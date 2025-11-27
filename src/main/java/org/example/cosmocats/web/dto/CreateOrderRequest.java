package org.example.cosmocats.web.dto;

import java.util.List;

public record CreateOrderRequest(
        String customerEmail,
        String customerFullName,
        String status,
        List<OrderLineDto> lines
) {
}
