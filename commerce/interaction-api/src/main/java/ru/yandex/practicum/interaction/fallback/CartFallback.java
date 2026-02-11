package ru.yandex.practicum.interaction.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.CartClient;
import ru.yandex.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class CartFallback implements CartClient {

    @Override
    public ShoppingCartDto getCart(String userName) {
        System.out.println("Fallback: getCart — cart service is unavailable, returning an empty cart.");
        return emptyCart();
    }

    @Override
    public ShoppingCartDto putProductsToCart(String userName, Map<UUID, Long> products) {
        System.out.println("Fallback: putProductsToCart — cart service is unavailable, products were not added.");
        return emptyCart();
    }

    @Override
    public void deactivateCart(String userName) {
        System.out.println("Fallback: deactivateCart — cart service is unavailable, deactivation was not performed.");
    }

    @Override
    public ShoppingCartDto removeProductFromCart(String userName, List<UUID> productIds) {
        System.out.println("Fallback: removeProductFromCart — cart service is unavailable, products were not removed.");
        return emptyCart();
    }

    @Override
    public ShoppingCartDto changeQuantityInCart(String userName, ChangeProductQuantityRequest request) {
        System.out.println("Fallback: changeQuantityInCart — cart service is unavailable, product quantities were not changed.");
        return emptyCart();
    }

    private ShoppingCartDto emptyCart() {
        return ShoppingCartDto.builder().build();
    }
}