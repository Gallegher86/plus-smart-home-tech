package ru.yandex.practicum.exceptions.delivery;

import java.util.List;

public class OrderValidationException extends RuntimeException {

    private final List<String> errors;

    public OrderValidationException(List<String> errors) {
        super("Order validation failed");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
