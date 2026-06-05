package ru.yandex.practicum.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.client.OrderClient;
import ru.yandex.practicum.dto.delivery.DeliveryDto;
import ru.yandex.practicum.dto.delivery.NewDeliveryDto;
import ru.yandex.practicum.dto.delivery.ShippedToDeliveryRequest;
import ru.yandex.practicum.dto.order.OrderDtoDelivery;
import ru.yandex.practicum.exceptions.warehouse.OrderBookingNotFoundException;
import ru.yandex.practicum.service.DeliveryService;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryFacadeImpl implements DeliveryFacade {
    private final DeliveryService service;
    private final OrderClient orderClient;
    private final WarehouseClientDeliveryFacade warehouseClient;

    @Override
    public DeliveryDto createDelivery(NewDeliveryDto request) {
        return service.createDelivery(request);
    }

    @Override
    public void handleSuccessfulDelivery(UUID deliveryId) {
        DeliveryDto result = service.handleSuccessfulDelivery(deliveryId);
        orderClient.handleSuccessfulDelivery(result.getOrderId());
    }

    @Override
    public void handlePickedDelivery(UUID deliveryId) {
        DeliveryDto result = service.handlePickedDelivery(deliveryId);
        UUID orderId = result.getOrderId();

        ShippedToDeliveryRequest request = ShippedToDeliveryRequest.builder()
                .orderId(orderId)
                .deliveryId(result.getDeliveryId())
                .build();

        warehouseClient.shipProductsToDelivery(request);
        orderClient.handlePickedDelivery(orderId);
    }

    @Override
    public void handleFailedDelivery(UUID deliveryId) {
        DeliveryDto result = service.handleFailedDelivery(deliveryId);
        orderClient.handleFailedDelivery(result.getOrderId());
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDtoDelivery request) {
        return service.calculateDeliveryCost(request);
    }
}
