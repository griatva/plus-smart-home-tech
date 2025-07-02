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

    @NotNull(message = "id заказа обязателен")
    private UUID orderId;

    @NotEmpty(message = "Список товаров для возврата не может быть пустым")
    private Map<UUID, Long> products;
}
