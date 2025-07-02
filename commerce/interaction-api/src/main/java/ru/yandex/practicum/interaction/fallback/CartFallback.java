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
        System.out.println("Fallback: getCart — корзина недоступна, возвращаем пустую корзину.");
        return emptyCart();
    }

    @Override
    public ShoppingCartDto putProductsToCart(String userName, Map<UUID, Long> products) {
        System.out.println("Fallback: putProductsToCart — корзина недоступна, товары не добавлены.");
        return emptyCart();
    }

    @Override
    public void deactivateCart(String userName) {
        System.out.println("Fallback: deactivateCart — корзина недоступна, деактивация не выполнена.");
    }

    @Override
    public ShoppingCartDto removeProductFromCart(String userName, List<UUID> productIds) {
        System.out.println("Fallback: removeProductFromCart — корзина недоступна, товары не удалены.");
        return emptyCart();
    }

    @Override
    public ShoppingCartDto changeQuantityInCart(String userName, ChangeProductQuantityRequest request) {
        System.out.println("Fallback: changeQuantityInCart — корзина недоступна, количество товаров не изменено.");
        return emptyCart();
    }

    private ShoppingCartDto emptyCart() {
        return ShoppingCartDto.builder().build();
    }
}