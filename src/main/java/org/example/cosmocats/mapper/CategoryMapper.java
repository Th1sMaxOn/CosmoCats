package org.example.cosmocats.mapper;

import org.example.cosmocats.domain.Category;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.web.dto.CategoryDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    // === Entity <-> Domain ===
    Category toDomain(CategoryEntity entity);
    CategoryEntity toEntity(Category domain);

    // === Domain <-> DTO ===
    CategoryDto toDto(Category domain);
    Category toDomain(CategoryDto dto);
}