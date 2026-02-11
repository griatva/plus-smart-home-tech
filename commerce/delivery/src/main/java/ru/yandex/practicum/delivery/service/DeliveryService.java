package ru.yandex.practicum.delivery.service;

import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface DeliveryService {

    DeliveryDto planDelivery(DeliveryDto deliveryDto);

    void markDeliverySuccessful(UUID orderId);

    void markDeliveryPicked(UUID orderId);

    void markDeliveryFailed(UUID orderId);

    BigDecimal calculateDeliveryCost(OrderDto orderDto);

}
