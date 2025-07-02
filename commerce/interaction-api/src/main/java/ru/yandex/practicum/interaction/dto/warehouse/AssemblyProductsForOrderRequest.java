package ru.yandex.practicum.interaction.dto.warehouse;


import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssemblyProductsForOrderRequest {

    @NotNull
    private UUID orderId;
    @NotNull
    private Map<UUID, Long> products;
}
