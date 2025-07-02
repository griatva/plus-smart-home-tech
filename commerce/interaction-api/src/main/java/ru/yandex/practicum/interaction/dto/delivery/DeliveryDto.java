package ru.yandex.practicum.interaction.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.enums.DeliveryState;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryDto {

    private UUID deliveryId;

    @NotNull(message = "Адрес склада не должен быть null")
    private AddressDto fromAddress;

    @NotNull(message = "Адрес получателя не должен быть null")
    private AddressDto toAddress;

    @NotNull(message = "orderId не должен быть null")
    private UUID orderId;

    @NotNull(message = "Статус доставки не должен быть null")
    private DeliveryState deliveryState;
}
