package ru.yandex.practicum.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.ShoppingStoreClient;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.exceptions.client.ShoppingStoreClientException;
import ru.yandex.practicum.exceptions.client.ShoppingStoreServiceUnavailableException;
import ru.yandex.practicum.exceptions.handler.ErrorCodes;
import ru.yandex.practicum.exceptions.handler.ErrorResponse;
import ru.yandex.practicum.exceptions.store.ProductNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingStoreClientPaymentFacade {
    private final ShoppingStoreClient shoppingStoreClient;
    private final ObjectMapper objectMapper;

    @CircuitBreaker(name = "shoppingStorePayment", fallbackMethod = "getProductByIdFallback")
    public ProductDto getProductById(UUID productId) {
        try {

            return shoppingStoreClient.getProductById(productId);

        } catch (FeignException.NotFound e) {

            try {

                ErrorResponse error = objectMapper.readValue(e.contentUTF8(), ErrorResponse.class);

                if (ErrorCodes.PRODUCT_IN_STORE_NOT_FOUND.equals(error.getError())) {
                    throw new ProductNotFoundException(error.getMessage());
                }

                throw new ShoppingStoreClientException("Неожиданная ошибка обработки ответа сервиса склада.", e);

            } catch (JsonProcessingException ex) {

                throw new ShoppingStoreClientException("Неожиданная ошибка ответа сервиса склада.", ex);
            }
        }
    }

    public ProductDto getProductByIdFallback(UUID productId, Throwable t) {
        throw new ShoppingStoreServiceUnavailableException(ErrorCodes.SHOPPING_STORE_SERVICE_UNAVAILABLE.getMessage(), t);
    }
}
