package ru.yandex.practicum.interaction.fallback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.OrderClient;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;

import java.util.Collections;
import java.util.UUID;

@Component
public class OrderFallback implements OrderClient {

    @Override
    public Page<OrderDto> getClientOrders(String username, int page, int size) {
        System.out.println("Fallback: getClientOrders — order service is unavailable, returning an empty page of orders.");
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        System.out.println("Fallback: createNewOrder — order service is unavailable, order was not created.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        System.out.println("Fallback: productReturn — order service is unavailable, product return was not performed.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto payment(UUID orderId) {
        System.out.println("Fallback: payment — order service is unavailable, payment was not recorded.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        System.out.println("Fallback: paymentFailed — order service is unavailable, payment failure was not recorded.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        System.out.println("Fallback: delivery — order service is unavailable, delivery was not recorded.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        System.out.println("Fallback: deliveryFailed — order service is unavailable, delivery failure was not recorded.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto complete(UUID orderId) {
        System.out.println("Fallback: complete — order service is unavailable, order completion was not recorded.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        System.out.println("Fallback: calculateTotalCost — order service is unavailable, total cost was not calculated.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        System.out.println("Fallback: calculateDeliveryCost — order service is unavailable, delivery cost was not calculated.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        System.out.println("Fallback: assembly — order service is unavailable, assembly was not recorded.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        System.out.println("Fallback: assemblyFailed — order service is unavailable, assembly failure was not recorded.");
        return emptyOrderDto();
    }

    private OrderDto emptyOrderDto() {
        return OrderDto.builder().build();
    }
}
