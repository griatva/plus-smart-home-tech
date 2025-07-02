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
        System.out.println("Fallback: getClientOrders — сервис заказов недоступен, возвращаем пустую страницу заказов.");
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {
        System.out.println("Fallback: createNewOrder — сервис заказов недоступен, заказ не создан.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto productReturn(ProductReturnRequest request) {
        System.out.println("Fallback: productReturn — сервис заказов недоступен, возврат не выполнен.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto payment(UUID orderId) {
        System.out.println("Fallback: payment — сервис заказов недоступен, оплата не зафиксирована.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto paymentFailed(UUID orderId) {
        System.out.println("Fallback: paymentFailed — сервис заказов недоступен, ошибка оплаты не зафиксирована.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto delivery(UUID orderId) {
        System.out.println("Fallback: delivery — сервис заказов недоступен, доставка не зафиксирована.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        System.out.println("Fallback: deliveryFailed — сервис заказов недоступен, ошибка доставки не зафиксирована.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto complete(UUID orderId) {
        System.out.println("Fallback: complete — сервис заказов недоступен, завершение заказа не зафиксировано.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        System.out.println("Fallback: calculateTotalCost — сервис заказов недоступен, стоимость не рассчитана.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        System.out.println("Fallback: calculateDeliveryCost — сервис заказов недоступен, стоимость доставки не рассчитана.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto assembly(UUID orderId) {
        System.out.println("Fallback: assembly — сервис заказов недоступен, сборка не зафиксирована.");
        return emptyOrderDto();
    }

    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        System.out.println("Fallback: assemblyFailed — сервис заказов недоступен, ошибка сборки не зафиксирована.");
        return emptyOrderDto();
    }

    private OrderDto emptyOrderDto() {
        return OrderDto.builder().build();
    }
}
