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

    @NotNull(message = "productId must not be null")
    private UUID productId;

    @NotNull(message = "newQuantity must not be null")
    @PositiveOrZero
    private Long newQuantity;
}
