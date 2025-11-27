package org.example.cosmocats.web;

import org.example.cosmocats.domain.Product;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.web.dto.ProductDto;
import org.example.cosmocats.web.projection.PopularProductProjection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> create(@RequestBody ProductDto dto) {
        Product p = new Product();
        p.setProductName(dto.productName());
        p.setDescription(dto.description());
        p.setPrice(dto.price());

        Product saved = productService.create(p, dto.categoryId());

        ProductDto response = new ProductDto(
                saved.getId(),
                saved.getProductName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getCategory().getId()
        );
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
                .body(response);
    }

    @GetMapping("/by-category/{categoryId}")
    public List<Product> byCategory(@PathVariable Long categoryId) {
        return productService.findByCategory(categoryId);
    }

    @GetMapping("/popular")
    public List<PopularProductProjection> popular(@RequestParam(defaultValue = "10") int limit) {
        return productService.mostPopular(limit);
    }
}
