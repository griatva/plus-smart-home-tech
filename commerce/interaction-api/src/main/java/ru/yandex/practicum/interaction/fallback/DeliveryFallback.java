package ru.yandex.practicum.interaction.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.DeliveryClient;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class DeliveryFallback implements DeliveryClient {

    @Override
    public DeliveryDto planDelivery(DeliveryDto deliveryDto) {
        System.out.println("Fallback: planDelivery — delivery service is unavailable, delivery was not created.");
        return emptyDeliveryDto(deliveryDto);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        System.out.println("Fallback: deliverySuccessful — delivery service is unavailable, successful delivery status was not set.");
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        System.out.println("Fallback: deliveryPicked — delivery service is unavailable, order pickup status was not set.");
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        System.out.println("Fallback: deliveryFailed — delivery service is unavailable, failed delivery status was not set.");
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        System.out.println("Fallback: calculateDeliveryCost — delivery service is unavailable, returning delivery cost 0.");
        return BigDecimal.ZERO;
    }

    private DeliveryDto emptyDeliveryDto(DeliveryDto deliveryDto) {
        return DeliveryDto.builder()
                .deliveryId(deliveryDto != null ? deliveryDto.getDeliveryId() : null)
                .fromAddress(deliveryDto != null ? deliveryDto.getFromAddress() : null)
                .toAddress(deliveryDto != null ? deliveryDto.getToAddress() : null)
                .orderId(deliveryDto != null ? deliveryDto.getOrderId() : null)
                .deliveryState(deliveryDto != null ? deliveryDto.getDeliveryState() : null)
                .build();
    }
}