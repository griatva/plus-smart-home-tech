package ru.yandex.practicum.order.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;

import java.util.UUID;

public interface OrderService {

    Page<OrderDto> getClientOrdersPaginated(String username, int page, int size);

    OrderDto createNewOrder(CreateNewOrderRequest request);

    OrderDto returnProduct(ProductReturnRequest productReturnRequest);

    OrderDto payment(UUID orderId);

    OrderDto paymentFailed(UUID orderId);

    OrderDto delivery(UUID orderId);

    OrderDto deliveryFailed(UUID orderId);

    OrderDto complete(UUID orderId);

    OrderDto calculateTotalCost(UUID orderId);

    OrderDto calculateDeliveryCost(UUID orderId);

    OrderDto assembly(UUID orderId);

    OrderDto assemblyFailed(UUID orderId);

}
