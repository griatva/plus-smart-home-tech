package ru.yandex.practicum.interaction.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNewOrderRequest {

    @NotNull(message = "Корзина не может быть null")
    private ShoppingCartDto shoppingCart;

    @NotNull(message = "Адрес доставки не может быть null")
    private AddressDto deliveryAddress;
}