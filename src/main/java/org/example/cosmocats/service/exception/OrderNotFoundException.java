package org.example.cosmocats.service.exception;

public class OrderNotFoundException extends ResourceNotFoundException {

    private static final String ERROR_MESSAGE = "Order not found with number: %s";

    public OrderNotFoundException(String orderNumber) {
        super(String.format(ERROR_MESSAGE, orderNumber));
    }
}