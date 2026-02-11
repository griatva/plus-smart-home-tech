package ru.yandex.practicum.interaction.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductReturnRequest {

    @NotNull(message = "orderId is required")
    private UUID orderId;

    @NotEmpty(message = "The list of products for return must not be empty")
    private Map<UUID, Long> products;
}
