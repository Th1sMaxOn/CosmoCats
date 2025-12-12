package org.example.cosmocats.service.exception;

public class ProductNotFoundException extends ResourceNotFoundException {

    private static final String ERROR_MESSAGE = "Product not found with identifier: %s";

    public ProductNotFoundException(Long id) {
        super(String.format(ERROR_MESSAGE, id));
    }
}
