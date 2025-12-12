package org.example.cosmocats.mapper;

import org.example.cosmocats.domain.Product;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.web.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    // === Entity <-> Domain ===
    @Mapping(target = "name", source = "productName") // В Entity це productName
    Product toDomain(ProductEntity entity);

    @Mapping(target = "productName", source = "name")
    ProductEntity toEntity(Product domain);

    // === Domain <-> DTO ===
    @Mapping(target = "productName", source = "name") // У DTO поле productName
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product domain);

    @Mapping(target = "name", source = "productName")
    @Mapping(target = "category.id", source = "categoryId") // Створюємо об'єкт Category лише з ID
    @Mapping(target = "category.name", ignore = true)
    @Mapping(target = "category.description", ignore = true)
    Product toDomain(ProductDto dto);
}