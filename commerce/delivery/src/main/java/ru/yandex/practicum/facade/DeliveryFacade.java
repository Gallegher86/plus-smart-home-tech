package ru.yandex.practicum.facade;

import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryFacade {
    DeliveryDto createDelivery(DeliveryDto request);

    void handleSuccessfulDelivery(UUID orderId);

    void handlePickedOrder(UUID orderId);

    void handleFailedDelivery(UUID orderId);

    BigDecimal calculateDeliveryCost(OrderDto request);
}
