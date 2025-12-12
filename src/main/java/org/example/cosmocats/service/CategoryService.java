package org.example.cosmocats.service;

import org.example.cosmocats.domain.Category;
import java.util.List;

public interface CategoryService {

    Category create(Category category);

    Category getById(Long id);

    List<Category> findAll();

    Category update(Long id, Category category);

    void delete(Long id);
}