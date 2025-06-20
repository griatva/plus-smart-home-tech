package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.service.WarehouseService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService service;

    @PutMapping
    public ResponseEntity<Void> createNewItem(@RequestBody @Valid NewProductInWarehouseRequest request) {
        log.info("Запрос на добавление на склад нового товара {}", request);
        service.createNewItem(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkQuantity(@RequestBody @Valid ShoppingCartDto cartDto) {
        log.info("Запрос на проверку наличия товаров на складе для корзины {}", cartDto);
        return ResponseEntity.ok(service.checkQuantity(cartDto));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProduct(@RequestBody @Valid AddProductToWarehouseRequest request) {
        log.info("Запрос на принятие товара на склад {}", request);
        service.addProduct(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("Запрос на получение адреса склада");
        return ResponseEntity.ok(service.getWarehouseAddress());
    }
}
