package org.example.cosmocats.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String customerEmail,

        @NotBlank(message = "Full name is required")
        String customerFullName,

        String status,

        @NotEmpty(message = "Order must contain at least one product")
        List<@jakarta.validation.Valid OrderLineDto> lines
) {
}