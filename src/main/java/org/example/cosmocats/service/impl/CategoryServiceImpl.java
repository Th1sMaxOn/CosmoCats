package org.example.cosmocats.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.cosmocats.domain.Category;
import org.example.cosmocats.repository.exception.PersistenceException;
import org.example.cosmocats.service.exception.CategoryNotFoundException;
import org.example.cosmocats.service.exception.ResourceNotFoundException;
import org.example.cosmocats.mapper.CategoryMapper;
import org.example.cosmocats.repository.CategoryRepository;
import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        try {
            return categoryRepository.findById(id)
                    .map(categoryMapper::toDomain)
                    .orElseThrow(() -> new CategoryNotFoundException(id));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to fetch Category", e);
        }
    }

    @Override
    @Transactional
    public Category update(Long id, Category category) {
        try {
            CategoryEntity entity = categoryRepository.findById(id)
                    .orElseThrow(() -> new CategoryNotFoundException(id));

            entity.setName(category.getName());
            entity.setDescription(category.getDescription());

            return categoryMapper.toDomain(categoryRepository.save(entity));
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Failed to update Category with id: " + id, e);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        try {
            categoryRepository.deleteById(id);
        } catch (Exception e) {
            throw new PersistenceException("Failed to delete Category with id: " + id, e);
        }
    }

    @Override
    @Transactional
    public Category create(Category category) {
        try {
            CategoryEntity entity = categoryMapper.toEntity(category);
            return categoryMapper.toDomain(categoryRepository.save(entity));
        } catch (Exception e) {
            throw new PersistenceException("Failed to save category: " + category.getName(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        try {
            return categoryRepository.findAll().stream()
                    .map(categoryMapper::toDomain)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new PersistenceException("Failed to fetch categories", e);
        }
    }
}