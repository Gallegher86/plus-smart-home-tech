package ru.yandex.practicum.exceptions.store;

public class ProductNotFoundExeception extends RuntimeException {
    public ProductNotFoundExeception(String message) {
        super(message);
    }
}
