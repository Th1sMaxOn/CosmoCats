package com.cosmo.cats.mapper;

import com.cosmo.cats.domain.model.Product;
import com.cosmo.cats.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toEntity(ProductDTO dto);
    ProductDTO toDto(Product entity);
}
