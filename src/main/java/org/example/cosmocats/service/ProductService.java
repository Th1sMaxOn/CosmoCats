package org.example.cosmocats.service;

import org.example.cosmocats.domain.Product;
import org.example.cosmocats.repository.projection.PopularProductProjection;
import java.util.List;

public interface ProductService {

    Product create(Product product);

    Product getById(Long id);

    List<Product> findByCategory(Long categoryId);

    List<Product> getAll();

    List<PopularProductProjection> mostPopular(int limit);

    Product update(Long id, Product product);

    void delete(Long id);
}