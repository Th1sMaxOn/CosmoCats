package com.cosmo.cats.dto;

import com.cosmo.cats.validation.CosmicWordCheck;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductDTO {
    private UUID id;

    @NotNull @Size(min=3, max=100)
    @CosmicWordCheck
    private String name;

    @Size(max=500)
    private String description;

    @NotNull @Positive
    private BigDecimal price;

    @NotNull @Size(min=3, max=3)
    private String currency;

    @NotNull @Size(min=1, max=50)
    private String categoryId;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
}
