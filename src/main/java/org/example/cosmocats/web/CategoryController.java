package org.example.cosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cosmocats.domain.Category;
import org.example.cosmocats.mapper.CategoryMapper;
import org.example.cosmocats.service.CategoryService;
import org.example.cosmocats.web.dto.CategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    // Створити
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'BOT')")
    public ResponseEntity<CategoryDto> create(@RequestBody @Valid CategoryDto dto) {
        Category domain = categoryMapper.toDomain(dto);
        Category saved = categoryService.create(domain);
        CategoryDto response = categoryMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/categories/" + response.id()))
                .body(response);
    }

    // Отримати всі
    @GetMapping
    public List<CategoryDto> findAll() {
        return categoryService.findAll().stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    // Отримати одну по ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return ResponseEntity.ok(categoryMapper.toDto(category));
    }

    // Оновити
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryDto> update(@PathVariable Long id, @RequestBody @Valid CategoryDto dto) {
        Category domain = categoryMapper.toDomain(dto);
        Category updated = categoryService.update(id, domain);
        return ResponseEntity.ok(categoryMapper.toDto(updated));
    }

    // Видалити
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}