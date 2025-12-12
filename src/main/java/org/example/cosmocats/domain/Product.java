package org.example.cosmocats.domain;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value
@Builder
public class Product {
    Long id;
    String name;
    String description;
    BigDecimal price;
    Category category;
}