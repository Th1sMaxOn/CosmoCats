package com.cosmo.cats.application.impl;

import com.cosmo.cats.application.ProductService;
import com.cosmo.cats.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.math.BigDecimal;

@Service
public class InMemoryProductService implements ProductService {

    private final Map<UUID, Product> store = new ConcurrentHashMap<>();

    public InMemoryProductService() {
        UUID id = UUID.randomUUID();
        store.put(id, new Product(
            id, "Galaxy Yarn Ball", "Anti-gravity yarn ball",
            new BigDecimal("42.00"), "CRD", "TOYS"));
    }

    @Override
    public Product create(Product product) {
        UUID id = Optional.ofNullable(product.getId()).orElse(UUID.randomUUID());
        product.setId(id);
        store.put(id, product);
        return product;
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Product> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Product update(UUID id, Product product) {
        if (!store.containsKey(id)) {
            throw new NoSuchElementException("Product not found: " + id);
        }
        product.setId(id);
        store.put(id, product);
        return product;
    }

    @Override
    public void delete(UUID id) {
        store.remove(id);
    }
}
