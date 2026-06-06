package ru.yandex.practicum.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.dto.order.OrderDto;
import ru.yandex.practicum.dto.order.ProductReturnRequest;
import ru.yandex.practicum.utils.PaginationConstants;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.ASC;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderClient {

    @GetMapping
    Page<OrderDto> getOrders(@RequestParam String username,
                             @PageableDefault(
                                     size = PaginationConstants.DEFAULT_PAGE_SIZE,
                                     sort = PaginationConstants.ORDER_STATE_SORT,
                                     direction = ASC) Pageable pageable);

    @PutMapping
    OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto handleReturn(@RequestBody @Valid ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto prepareOrderForPayment(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/successful")
    OrderDto handleSuccessfulPayment(@RequestBody @NotNull UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto handleFailedPayment(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly")
    OrderDto handleSuccessfulAssembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto handleFailedAssembly(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery")
    OrderDto handlePickedDelivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/successful")
    OrderDto handleSuccessfulDelivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto handleFailedDelivery(@RequestBody @NotNull UUID orderId);

    @PostMapping("/completed")
    OrderDto handleComplete(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto handleCalculateTotalPrice(@RequestBody @NotNull UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto handleCalculateDeliveryPrice(@RequestBody @NotNull UUID orderId);
}
