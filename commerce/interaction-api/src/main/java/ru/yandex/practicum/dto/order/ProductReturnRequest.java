package ru.yandex.practicum.dto.order;

import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.UUID;

@Value
@Builder
public class ProductReturnRequest {
    UUID orderId;
    Map<UUID, Long> products;
}
