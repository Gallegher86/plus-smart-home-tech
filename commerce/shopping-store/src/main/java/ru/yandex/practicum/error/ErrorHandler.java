package ru.yandex.practicum.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.exceptions.handler.ErrorResponse;
import ru.yandex.practicum.exceptions.store.ProductNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .toList();

        logValidationError(ex, errors);

        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST)
                .error("Validation failed")
                .message("Ошибки валидации данных")
                .userMessage("Проверьте корректность заполнения полей")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .validationErrors(errors)
                .build();
    }

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFoundException(
            ProductNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn("Товар не найден: {}", ex.getMessage());

        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND)
                .error("Product not found")
                .message(ex.getMessage())
                .userMessage("Запрашиваемый товар не найден")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error("Внутренняя ошибка сервера", ex);

        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .error("Internal server error")
                .message("Unexpected server error")
                .userMessage("На сервере произошла ошибка. Попробуйте позже")
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private String formatFieldError(FieldError error) {
        return String.format("Поле '%s': %s", error.getField(), error.getDefaultMessage());
    }

    private void logValidationError(MethodArgumentNotValidException ex, List<String> errors) {
        log.warn("Валидация не пройдена: {} ошибок в {} полях. Детали: {}",
                errors.size(),
                ex.getBindingResult().getErrorCount(),
                errors);
    }
}
