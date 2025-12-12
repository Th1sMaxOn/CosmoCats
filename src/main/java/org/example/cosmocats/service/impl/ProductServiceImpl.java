package org.example.cosmocats.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cosmocats.domain.Product;
import org.example.cosmocats.service.exception.CategoryNotFoundException;
import org.example.cosmocats.repository.exception.PersistenceException;
import org.example.cosmocats.service.exception.ProductNotFoundException;
import org.example.cosmocats.service.exception.ResourceNotFoundException;
import org.example.cosmocats.mapper.ProductMapper;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.ProductRepository;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.repository.entity.ProductEntity;
import org.example.cosmocats.service.ProductService;
import org.example.cosmocats.repository.projection.PopularProductProjection;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Product create(Product product) {
        try {
            ProductEntity entity = productMapper.toEntity(product);

            if (entity.getCategory() != null && entity.getCategory().getId() != null) {
                Long catId = entity.getCategory().getId();
                CategoryEntity category = categoryRepository.findById(catId)
                        .orElseThrow(() -> new CategoryNotFoundException(catId));
                entity.setCategory(category);
            }

            return productMapper.toDomain(productRepository.save(entity));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to create Product", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Product getById(Long id) {
        try {
            return productRepository.findById(id)
                    .map(productMapper::toDomain)
                    .orElseThrow(() -> new ProductNotFoundException(id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to fetch Product", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {
        try {
            if (!categoryRepository.existsById(categoryId)) {
                throw new CategoryNotFoundException(categoryId);
            }
            return productRepository.findByCategoryId(categoryId).stream()
                    .map(productMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error finding products by category id: " + categoryId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAll() {
        try {
            return productRepository.findAll().stream()
                    .map(productMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenceException("Error fetching products", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PopularProductProjection> mostPopular(int limit) {
        try {
            return productRepository.findMostPopularProducts(PageRequest.of(0, limit));
        } catch (Exception e) {
            throw new PersistenceException("Error fetching popular products", e);
        }
    }

    @Override
    @Transactional
    public Product update(Long id, Product product) {
        try {
            ProductEntity entity = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));

            entity.setProductName(product.getName());
            entity.setDescription(product.getDescription());
            entity.setPrice(product.getPrice());

            if (product.getCategory() != null && product.getCategory().getId() != null) {
                Long catId = product.getCategory().getId();
                CategoryEntity category = categoryRepository.findById(catId)
                        .orElseThrow(() -> new CategoryNotFoundException(catId));
                entity.setCategory(category);
            }

            return productMapper.toDomain(productRepository.save(entity));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to update Product with id: " + id, e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (Exception e) {
            throw new PersistenceException("Failed to delete Product with id: " + id, e);
        }
    }
}