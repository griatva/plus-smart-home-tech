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
        log.info("Request to add a new product to the warehouse: {}", request);
        service.createNewItem(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/check")
    public ResponseEntity<BookedProductsDto> checkQuantity(@RequestBody @Valid ShoppingCartDto cartDto) {
        log.info("Request to check product availability in the warehouse for cart: {}", cartDto);
        return ResponseEntity.ok(service.checkQuantity(cartDto));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addProduct(@RequestBody @Valid AddProductToWarehouseRequest request) {
        log.info("Request to accept products into the warehouse: {}", request);
        service.addProduct(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/address")
    public ResponseEntity<AddressDto> getWarehouseAddress() {
        log.info("Request to retrieve the warehouse address");
        return ResponseEntity.ok(service.getWarehouseAddress());
    }

    @PostMapping("/shipped")
    public ResponseEntity<Void> shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        log.info("Request to ship products to delivery: {}", request);
        service.shippedToDelivery(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/return")
    public ResponseEntity<Void> acceptReturn(@RequestBody @Valid ProductReturnMapRequest request) {
        log.info("Request to return products to the warehouse: {}", request);
        service.acceptReturn(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/assembly")
    public ResponseEntity<BookedProductsDto> assemblyProductsForOrder(
            @RequestBody @Valid AssemblyProductsForOrderRequest request) {
        log.info("Request to assemble products for order: {}", request);
        return ResponseEntity.ok(service.assemblyProductsForOrder(request));
    }
}
