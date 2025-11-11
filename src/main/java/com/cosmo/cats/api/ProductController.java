package com.cosmo.cats.api;

import com.cosmo.cats.application.ProductService;
import com.cosmo.cats.domain.model.Product;
import com.cosmo.cats.dto.ProductDTO;
import com.cosmo.cats.mapper.ProductMapper;
import com.cosmo.cats.integration.PriceRateClient;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final ProductMapper mapper;
    private final PriceRateClient rateClient;

    public ProductController(ProductService service, ProductMapper mapper, PriceRateClient rateClient) {
        this.service = service;
        this.mapper = mapper;
        this.rateClient = rateClient;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        Product created = service.create(mapper.toEntity(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toDto(created));
    }

    @GetMapping
    public ResponseEntity<java.util.List<ProductDTO>> findAll(
            @RequestParam(value = "convertTo", required = false) String convertTo
    ) {
        var list = service.findAll().stream().map(p -> {
            var d = mapper.toDto(p);
            if (convertTo != null && !convertTo.isBlank()) {
                var rate = rateClient.getRate(p.getCurrency(), convertTo);
                d.setCurrency(convertTo);
                d.setPrice(p.getPrice().multiply(rate.getRate()));
            }
            return d;
        }).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> get(@PathVariable("id") UUID id) {
        var entity = service.findById(id)
                .orElseThrow(() -> new java.util.NoSuchElementException("Product not found: " + id));
        return ResponseEntity.ok(mapper.toDto(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(@PathVariable("id") UUID id,
                                             @Valid @RequestBody ProductDTO dto) {
        var updated = service.update(id, mapper.toEntity(dto));
        return ResponseEntity.ok(mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
