package ru.yandex.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentClient {
    @PostMapping
    PaymentDto handlePayment(@RequestBody OrderDto request);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody OrderDto request);

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@RequestBody OrderDto request);

    @PostMapping("/refund")
    void handleSuccessfulPayment(@RequestBody UUID paymentId);

    @PostMapping("/failed")
    void handleFailedPayment(@RequestBody UUID paymentId);
}
