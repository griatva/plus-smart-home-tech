package ru.yandex.practicum.cart.service;

import ru.yandex.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CartService {

    ShoppingCartDto getCart(String userName);

    ShoppingCartDto putProductsToCart(String userName, Map<UUID, Long> products);

    void deactivateCart(String userName);

    ShoppingCartDto removeProductFromCart(String userName, List<UUID> productIds);

    ShoppingCartDto changeQuantityInCart(String userName, ChangeProductQuantityRequest request);

}
