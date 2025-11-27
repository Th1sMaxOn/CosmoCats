package org.example.cosmocats.web;

import org.example.cosmocats.domain.Category;
import org.example.cosmocats.service.CategoryService;
import org.example.cosmocats.web.dto.CategoryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CategoryDto dto) {
        Category c = new Category(null, dto.name(), dto.description());
        Category saved = categoryService.create(c);
        CategoryDto response = new CategoryDto(saved.getId(), saved.getName(), saved.getDescription());
        return ResponseEntity.created(URI.create("/api/categories/" + saved.getId()))
                .body(response);
    }

    @GetMapping
    public List<Category> all() {
        return categoryService.findAll();
    }
}
