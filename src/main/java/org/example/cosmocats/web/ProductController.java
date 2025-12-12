package org.example.cosmocats.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.mapper.ProductMapper;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.web.dto.ProductDto;
import org.example.cosmocats.repository.projection.PopularProductProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    // Створити
    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody @Valid ProductDto dto) {
        Product domain = productMapper.toDomain(dto);
        Product saved = productService.create(domain);
        ProductDto response = productMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/products/" + response.id()))
                .body(response);
    }

    // Отримати один продукт
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        return ResponseEntity.ok(productMapper.toDto(product));
    }

    // Отримати всі
    @GetMapping
    public List<ProductDto> getAll() {
        return productService.getAll().stream()
                .map(productMapper::toDto)
                .toList();
    }

    // Фільтр по категорії
    @GetMapping("/by-category/{categoryId}")
    public List<ProductDto> byCategory(@PathVariable Long categoryId) {
        return productService.findByCategory(categoryId).stream()
                .map(productMapper::toDto)
                .toList();
    }

    // Статистика (Проекції віддаємо напряму, мапити не обов'язково)
    @GetMapping("/popular")
    public List<PopularProductProjection> popular(@RequestParam(defaultValue = "10") int limit) {
        return productService.mostPopular(limit);
    }

    // Оновити
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody @Valid ProductDto dto) {
        Product domain = productMapper.toDomain(dto);
        Product updated = productService.update(id, domain);
        return ResponseEntity.ok(productMapper.toDto(updated));
    }

    // Видалити
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}