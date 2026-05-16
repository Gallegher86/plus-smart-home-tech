package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.dto.store.NewProductDto;
import ru.yandex.practicum.dto.store.ProductDto;
import ru.yandex.practicum.dto.store.SetProductQuantityState;
import ru.yandex.practicum.dto.store.UpdatedProductDto;
import ru.yandex.practicum.enums.ProductCategory;
import ru.yandex.practicum.service.ShoppingStoreService;
import ru.yandex.practicum.utils.PaginationConstants;

import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api/v1/shopping-store")
@RequiredArgsConstructor
public class ShoppingStoreController {
    private final ShoppingStoreService shoppingStoreService;

    @GetMapping
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category,
                                        @PageableDefault(
                                                size = PaginationConstants.DEFAULT_PAGE_SIZE,
                                                sort = PaginationConstants.DEFAULT_SORT,
                                                direction = ASC) Pageable pageable) {
        log.info("Получен GET-запрос на просмотр товаров категории {} с пагинацией {}.",
                category, pageable);
        return shoppingStoreService.getProductsByCategory(category, pageable);
    }

    @PutMapping
    public  ProductDto addNewProduct(@RequestBody @Valid NewProductDto newProduct) {
        log.info("Получен PUT-запрос на добавление нового товара {}.", newProduct);
        return shoppingStoreService.addNewProduct(newProduct);
    }

    @PostMapping
    public ProductDto updateExistingProduct(@RequestBody @Valid UpdatedProductDto updatedProduct) {
        log.info("Получен POST-запрос на обновление данных товара {}.", updatedProduct);
        return shoppingStoreService.updateExistingProduct(updatedProduct);
    }

    @PostMapping("/removeProductFromStore")
    public boolean removeProduct(@RequestBody UUID productId) {
        log.info("Получен POST-запрос на удаление товара с productId {} из магазина.", productId);
        return shoppingStoreService.removeProduct(productId);
    }

    @PostMapping("/quantityState")
    public boolean setProductQuantityState(@RequestBody @Valid SetProductQuantityState quantityState) {
        log.info("Получен POST-запрос на изменение количества товара {}.", quantityState);
        return shoppingStoreService.setProductQuantityState(quantityState);
    }

    @GetMapping("/{productId}")
    public ProductDto getProductById(@RequestParam UUID productId) {
        log.info("Получен GET-запрос на просмотр товара с productId {}.", productId);
        return shoppingStoreService.getProductById(productId);
    }

}
