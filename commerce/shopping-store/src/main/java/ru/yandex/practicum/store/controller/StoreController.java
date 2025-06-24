package ru.yandex.practicum.store.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.store.PageableDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.QuantityState;
import ru.yandex.practicum.store.service.StoreService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-store")
public class StoreController {

    private final StoreService service;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProductsByCategoryPageable(
            @RequestParam @Valid ProductCategory category, @Valid PageableDto pageableDto) {
        log.info("Запрос на получение списка товаров. Category = {}, pageable = {}", category, pageableDto);
        return ResponseEntity.ok(service.getProductsByCategoryPageable(category, pageableDto));
    }

    @PutMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Запрос на создание нового товара {}", productDto);
        return ResponseEntity.ok(service.createProduct(productDto));
    }

    @PostMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestBody @Valid ProductDto productDto) {
        log.info("Запрос на обновление товара {}", productDto);
        return ResponseEntity.ok(service.updateProduct(productDto));
    }


    @PostMapping("/removeProductFromStore")
    public ResponseEntity<Boolean> removeProduct(@RequestBody UUID productId) {
        log.info("Запрос на удаление товара с id: {}", productId);
        return ResponseEntity.ok(service.deactivateProduct(productId));
    }


    @PostMapping("/quantityState")
    public ResponseEntity<Boolean> updateQuantityState(
            @RequestParam @NotNull UUID productId,
            @RequestParam @NotNull QuantityState quantityState
    ) {
        log.info("Запрос на изменение статуса остатка товара: productId={}, quantityState={}", productId, quantityState);
        return ResponseEntity.ok(service.updateQuantityState(productId, quantityState));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID productId) {
        log.info("Запрос на получение товара по id: {}", productId);
        return ResponseEntity.ok(service.getProductById(productId));
    }
}
