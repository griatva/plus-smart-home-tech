package ru.yandex.practicum.interaction.dto.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangeProductQuantityRequest {

    @NotNull(message = "productId не может быть null")
    private UUID productId;

    @NotNull(message = "newQuantity не может быть null")
    @PositiveOrZero
    private Long newQuantity;
}
