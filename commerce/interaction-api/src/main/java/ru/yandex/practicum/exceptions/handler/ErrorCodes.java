package ru.yandex.practicum.exceptions.handler;

public enum ErrorCodes {
    VALIDATION_FAILED("Ошибка валидации данных."),
    PRODUCT_NOT_FOUND("Товар не найден."),
    INTERNAL_SERVER_ERROR("Внутренняя ошибка сервера."),
    NO_PRODUCT_IN_WAREHOUSE("Товар не зарегистрирован на складе."),
    PRODUCT_ALREADY_IN_WAREHOUSE("Товар уже зарегистрирован на складе."),
    LOW_QUANTITY_IN_WAREHOUSE("Недостаточно товара на складе."),
    WAREHOUSE_SERVICE_UNAVAILABLE("Сервер склада недоступен."),
    NOT_AUTHORIZED_USER("Авторизация пользователя не пройдена."),
    SHOPPING_CART_NOT_FOUND("Корзина не найдена."),
    PRODUCT_IN_CART_NOT_FOUND("Товар в корзине не найден."),
    ORDER_BOOKING_ALREADY_EXIST("Товар на складе для заказа уже забронирован."),
    ORDER_BOOKING_NOT_FOUND("Бронь на складе для заказа не найдена.");

    private final String message;

    ErrorCodes(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
