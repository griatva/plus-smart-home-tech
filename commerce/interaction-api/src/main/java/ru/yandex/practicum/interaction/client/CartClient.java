package ru.yandex.practicum.interaction.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartClient {

    @GetMapping
    ShoppingCartDto getCart(@RequestParam String userName);

    @PutMapping
    ShoppingCartDto putProductsToCart(@RequestParam String userName, @RequestBody Map<UUID, Long> products);

    @DeleteMapping
    void deactivateCart(@RequestParam String userName);

    @PostMapping("/remove")
    ShoppingCartDto removeProductFromCart(@RequestParam String userName, @RequestBody List<UUID> productIds);

    @PostMapping("/change-quantity")
    ShoppingCartDto changeQuantityInCart(@RequestParam String userName, @RequestBody ChangeProductQuantityRequest request);
}