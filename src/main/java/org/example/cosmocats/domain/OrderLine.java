package org.example.cosmocats.domain;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value
@Builder
public class OrderLine {
    Long id;
    Product product;
    Integer quantity;
    BigDecimal priceAtPurchase;
}