package ru.yandex.practicum.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.mapper.ShoppingCartMapper;
import ru.yandex.practicum.model.ShoppingCart;
import ru.yandex.practicum.service.ShoppingCartService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShoppingCartFacadeImpl implements ShoppingCartFacade{
    private final ShoppingCartService service;
    private final WarehouseClientFacade warehouseClient;
    private final ShoppingCartMapper mapper;

    @Override
    public ShoppingCartDto getShoppingCart(String username) {
        ShoppingCart shoppingCart = service.getShoppingCart(username);
        return mapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public void deactivateShoppingCart(String username) {
        service.deactivateShoppingCart(username);
    }

    @Override
    public ShoppingCartDto removeProducts(String username, List<UUID> products) {
        ShoppingCart shoppingCart = service.removeProducts(username, products);
        return mapper.toShoppingCartDto(shoppingCart);
    }

    @Override
    public ShoppingCartDto addProducts(String username, Map<UUID, Long> products) {
        ShoppingCart cart = service.getShoppingCart(username);
        ShoppingCartDto draft = mapper.toShoppingCartDto(cart);
        draft.getProducts().putAll(products);
        warehouseClient.checkShoppingCart(draft);
        cart = service.addProducts(username, products);
        return mapper.toShoppingCartDto(cart);
    }

    @Override
    public ShoppingCartDto changeProductsQuantity(String username, ChangeProductQuantityRequest request) {
        ShoppingCart shoppingCart = service.changeProductsQuantity(username, request);
        return mapper.toShoppingCartDto(shoppingCart);
    }
}
