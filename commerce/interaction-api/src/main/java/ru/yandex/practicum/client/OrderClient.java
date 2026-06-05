package ru.yandex.practicum.client;

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
                                              sort = PaginationConstants.DEFAULT_SORT,
                                              direction = ASC) Pageable pageable);

    @PutMapping
    OrderDto createOrder(@RequestBody CreateNewOrderRequest request);

    @PostMapping("/return")
    OrderDto handleReturn(@RequestBody ProductReturnRequest request);

    @PostMapping("/payment")
    OrderDto prepareOrderForPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/successful")
    OrderDto handleSuccessfulPayment(@RequestBody UUID orderId);

    @PostMapping("/payment/failed")
    OrderDto handleFailedPayment(@RequestBody UUID orderId);

    @PostMapping("/assembly")
    OrderDto handleSuccessfulAssembly(@RequestBody UUID orderId);

    @PostMapping("/assembly/failed")
    OrderDto handleFailedAssembly(@RequestBody UUID orderId);

    @PostMapping("/delivery")
    OrderDto handlePickedDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/successful")
    OrderDto handleSuccessfulDelivery(@RequestBody UUID orderId);

    @PostMapping("/delivery/failed")
    OrderDto handleFailedDelivery(@RequestBody UUID orderId);

    @PostMapping("/completed")
    OrderDto handleComplete(@RequestBody UUID orderId);

    @PostMapping("/calculate/total")
    OrderDto handleCalculateTotalPrice(@RequestBody UUID orderId);

    @PostMapping("/calculate/delivery")
    OrderDto handleCalculateDeliveryPrice(@RequestBody UUID orderId);
}
