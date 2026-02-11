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

    @NotNull(message = "Warehouse address must not be null")
    private AddressDto fromAddress;

    @NotNull(message = "Recipient address must not be null")
    private AddressDto toAddress;

    @NotNull(message = "orderId must not be null")
    private UUID orderId;

    @NotNull(message = "Delivery status must not be null")
    private DeliveryState deliveryState;
}
