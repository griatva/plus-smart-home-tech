package ru.yandex.practicum.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.cart.service.CartService;
import ru.yandex.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-cart")
public class CartController {

    private final CartService service;

    @GetMapping
    public ResponseEntity<ShoppingCartDto> getCart(@RequestParam String username) {
        log.info("Received request to get shopping cart for user: {}", username);
        return ResponseEntity.ok(service.getCart(username));
    }

    @PutMapping
    public ResponseEntity<ShoppingCartDto> putProductsToCart(@RequestParam String username,
                                                             @RequestBody Map<UUID, Long> products) {
        log.info("Received request to add products {} to shopping cart for user: {}", products, username);
        return ResponseEntity.ok(service.putProductsToCart(username, products));
    }

    @DeleteMapping
    public ResponseEntity<Void> deactivateCart(@RequestParam String username) {
        log.info("Received request to deactivate shopping cart for user: {}", username);
        service.deactivateCart(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<ShoppingCartDto> removeProductFromCart(@RequestParam String username,
                                                                 @RequestBody List<UUID> productIds) {
        log.info("Received request to remove products with ids {} from shopping cart for user: {}", productIds, username);
        return ResponseEntity.ok(service.removeProductFromCart(username, productIds));
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<ShoppingCartDto> changeQuantityInCart(
            @RequestParam String username,
            @RequestBody @Valid ChangeProductQuantityRequest request) {
        log.info("Received request to change product quantity {} in shopping cart for user: {}", request, username);
        return ResponseEntity.ok(service.changeQuantityInCart(username, request));
    }
}
