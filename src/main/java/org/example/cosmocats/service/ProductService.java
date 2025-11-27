package org.example.cosmocats.service;

import org.example.cosmocats.domain.Category;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.feature.ToggleFeature;   // ДОДАЙ цей import
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.web.projection.PopularProductProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(Product p, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
        p.setCategory(category);
        return productRepository.save(p);
    }

    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Transactional(readOnly = true)
    public List<PopularProductProjection> mostPopular(int limit) {
        return productRepository.findMostPopularProducts(PageRequest.of(0, limit));
    }

    // 🔥 Новий метод для feature-toggle тестів
    @Transactional(readOnly = true)
    @ToggleFeature("cosmoCats")          // ключ можеш змінити, якщо в анотації/сервісі інший
    public List<Product> getAll() {
        return productRepository.findAll();
    }
}
