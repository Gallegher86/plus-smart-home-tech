package ru.yandex.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.client.DeliveryClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.facade.DeliveryFacade;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/v1/delivery")
@RequiredArgsConstructor
public class DeliveryController implements DeliveryClient {
    private final DeliveryFacade deliveryFacade;

    @PutMapping
    public DeliveryDto createDelivery(@RequestBody DeliveryDto request) {
        log.info("Получен PUT-запрос на создание доставки заказа с id {}.", request.getOrderId());
        return deliveryFacade.createDelivery(request);
    }

    @PostMapping("/successful")
    public void handleSuccessfulDelivery(@RequestBody UUID orderId) {
        log.info("Получен POST-запрос на фиксацию успешной доставки заказа с id {}.", orderId);
        deliveryFacade.handleSuccessfulDelivery(orderId);
    }

    @PostMapping("/picked")
    public void handlePickedOrder(@RequestBody UUID orderId) {
        log.info("Получен POST-запрос на получение товара в доставку заказа с id {}.", orderId);
        deliveryFacade.handlePickedOrder(orderId);
    }

    @PostMapping("/failed")
    public void handleFailedDelivery(@RequestBody UUID orderId) {
        log.info("Получен POST-запрос на фиксацию ошибки в доставке заказа с id {}.", orderId);
        deliveryFacade.handleFailedDelivery(orderId);
    }

    @PostMapping("/cost")
    public BigDecimal calculateDeliveryCost(@RequestBody OrderDto request) {
        log.info("Получен POST-запрос на расчет стоимости доставки заказа с id {}.", request.getOrderId());
        return deliveryFacade.calculateDeliveryCost(request);
    }
}
