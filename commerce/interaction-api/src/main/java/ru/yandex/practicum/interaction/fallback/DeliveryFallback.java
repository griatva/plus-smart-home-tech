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
        System.out.println("Fallback: planDelivery — сервис доставки недоступен, доставка не создана.");
        return emptyDeliveryDto(deliveryDto);
    }

    @Override
    public void deliverySuccessful(UUID orderId) {
        System.out.println("Fallback: deliverySuccessful — сервис доставки недоступен, отметка успешной доставки не выполнена.");
    }

    @Override
    public void deliveryPicked(UUID orderId) {
        System.out.println("Fallback: deliveryPicked — сервис доставки недоступен, отметка получения заказа в доставку не выполнена.");
    }

    @Override
    public void deliveryFailed(UUID orderId) {
        System.out.println("Fallback: deliveryFailed — сервис доставки недоступен, отметка неудачной доставки не выполнена.");
    }

    @Override
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) {
        System.out.println("Fallback: calculateDeliveryCost — сервис доставки недоступен, возвращаем стоимость доставки 0.");
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