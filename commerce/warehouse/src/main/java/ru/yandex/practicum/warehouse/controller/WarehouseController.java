package ru.yandex.practicum.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.*;
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

    @PostMapping("/shipped")
    public ResponseEntity<Void> shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        log.info("Запрос на передачу товаров в доставку {}", request);
        service.shippedToDelivery(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> acceptReturn(@RequestBody @Valid ProductReturnMapRequest request) {
        log.info("Запрос на возврат товаров на склад {}", request);
        service.acceptReturn(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assembly")
    public ResponseEntity<BookedProductsDto> assemblyProductsForOrder(
            @RequestBody @Valid AssemblyProductsForOrderRequest request) {
        log.info("Запрос на сборку товаров к заказу {}", request);
        return ResponseEntity.ok(service.assemblyProductsForOrder(request));
    }
}
