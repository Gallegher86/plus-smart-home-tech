package ru.yandex.practicum.facade;

import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentFacade {
    PaymentDto handlePayment(OrderDto request);

    BigDecimal calculateTotalCost(OrderDto request);

    BigDecimal calculateProductCost(OrderDto request);

    void handleSuccessfulPayment(UUID paymentId);

    void handleFailedPayment(UUID paymentId);
}
