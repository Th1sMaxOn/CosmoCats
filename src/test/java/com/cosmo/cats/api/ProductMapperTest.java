package com.cosmo.cats.mapper;

import com.cosmo.cats.domain.model.Product;
import com.cosmo.cats.dto.ProductDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void shouldMapEntityToDto() {
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Galaxy Sword");
        product.setDescription("Legendary blade");
        product.setPrice(new BigDecimal("9.99"));
        product.setCurrency("USD");
        product.setCategoryId("cat-123");

        ProductDTO dto = mapper.toDto(product);

        assertThat(dto).isNotNull();
        assertThat(dto.getName()).isEqualTo(product.getName());
        assertThat(dto.getPrice()).isEqualTo(product.getPrice());
        assertThat(dto.getCurrency()).isEqualTo(product.getCurrency());
    }

    @Test
    void shouldMapDtoToEntity() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Star Shield");
        dto.setDescription("Cosmic shield");
        dto.setPrice(new BigDecimal("5.00"));
        dto.setCurrency("USD");
        dto.setCategoryId("cat-999");

        Product product = mapper.toEntity(dto);

        assertThat(product).isNotNull();
        assertThat(product.getName()).isEqualTo(dto.getName());
        assertThat(product.getPrice()).isEqualTo(dto.getPrice());
        assertThat(product.getCurrency()).isEqualTo(dto.getCurrency());
    }
}
