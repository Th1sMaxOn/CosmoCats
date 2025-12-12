package org.example.cosmocats.service.exception;

public class CategoryNotFoundException extends ResourceNotFoundException {

    private static final String ERROR_MESSAGE = "Category not found with identifier: %s";

    public CategoryNotFoundException(Long id) {
        super(String.format(ERROR_MESSAGE, id));
    }
}
