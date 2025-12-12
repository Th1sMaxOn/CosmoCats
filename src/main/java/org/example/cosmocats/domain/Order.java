package org.example.cosmocats.domain;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class Order {
    Long id;
    String orderNumber;
    Customer customer;
    String status;
    Instant createdAt;

    @Builder.Default
    List<OrderLine> lines = new ArrayList<>();
}