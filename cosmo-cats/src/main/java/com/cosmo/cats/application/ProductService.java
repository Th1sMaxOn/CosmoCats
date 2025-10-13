package com.cosmo.cats.application;

import com.cosmo.cats.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductService {
    Product create(Product product);
    List<Product> findAll();
    Optional<Product> findById(UUID id);
    Product update(UUID id, Product product);
    void delete(UUID id);
}
