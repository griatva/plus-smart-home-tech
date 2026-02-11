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

    @NotNull(message = "Shopping cart must not be null")
    private ShoppingCartDto shoppingCart;

    @NotNull(message = "Delivery address must not be null")
    private AddressDto deliveryAddress;
}