package ru.yandex.practicum.cart.mapper;

import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;

public class CartMapper {

    public static ShoppingCartDto toDto(ShoppingCart cart) {

        if (cart == null) return null;

        return ShoppingCartDto.builder()
                .shoppingCartId(cart.getShoppingCartId())
                .products(cart.getProducts())
                .build();

    }

}
