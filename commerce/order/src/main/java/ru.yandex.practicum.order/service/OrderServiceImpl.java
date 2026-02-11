package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.client.CartClient;
import ru.yandex.practicum.interaction.client.DeliveryClient;
import ru.yandex.practicum.interaction.client.PaymentClient;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.AssemblyProductsForOrderRequest;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.ProductReturnMapRequest;
import ru.yandex.practicum.interaction.enums.DeliveryState;
import ru.yandex.practicum.interaction.enums.OrderState;
import ru.yandex.practicum.interaction.exception.NoOrderFoundException;
import ru.yandex.practicum.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.exception.ProductReturnException;
import ru.yandex.practicum.order.mapper.OrderMapper;
import ru.yandex.practicum.order.model.Order;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final DeliveryClient deliveryClient;
    private final WarehouseClient warehouseClient;
    private final PaymentClient paymentClient;

    private final OrderMapper mapper;

    @Override
    public Page<OrderDto> getClientOrdersPaginated(String username, int page, int size) {
        // User authorization (for now, we only check that the username is not empty) — 401 NotAuthorizedUserException
        checkUserName(username);
        Pageable pageRequest = PageRequest.of(page, size);

        ShoppingCartDto cart = cartClient.getCart(username);
        Page<Order> ordersPage = orderRepository.findAllByShoppingCartId(cart.getShoppingCartId(), pageRequest);
        return ordersPage.map(mapper::toDto);
    }

    private void checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
    }

    @Transactional
    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {

        // Create a new order and populate it with existing data: cartId, products, state
        ShoppingCartDto shoppingCart = request.getShoppingCart();
        AddressDto deliveryAddress = request.getDeliveryAddress();

        if (shoppingCart.getProducts() == null || shoppingCart.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order: shopping cart is empty.");
        }

        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Delivery address is not specified.");
        }

        Order order = Order.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getProducts())
                .state(OrderState.NEW)
                .build();

        order = orderRepository.save(order);
        UUID orderId = order.getOrderId();

        // Assemble the order in the warehouse and get weight, volume, and fragility info
        AssemblyProductsForOrderRequest bookRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(orderId)
                .products(shoppingCart.getProducts())
                .build();
        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProductsForOrder(bookRequest);

        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());

        // Get warehouse address
        AddressDto fromAddress = warehouseClient.getWarehouseAddress();

        // Create delivery and store its id in the order
        DeliveryDto deliveryDtoToPlan = DeliveryDto.builder()
                .fromAddress(fromAddress)
                .toAddress(deliveryAddress)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();

        DeliveryDto deliveryDtoCreated = deliveryClient.planDelivery(deliveryDtoToPlan);
        order.setDeliveryId(deliveryDtoCreated.getDeliveryId());

        // Request delivery cost from the delivery service
        BigDecimal deliveryCost = deliveryClient.calculateDeliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);

        // Request product cost and total cost from the payment service
        BigDecimal productCost = paymentClient.calculateProductCost(mapper.toDto(order));
        order.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.calculateTotalCost(mapper.toDto(order));
        order.setTotalPrice(totalCost);

        // Create payment and store its id
        PaymentDto paymentDto = paymentClient.createPayment(mapper.toDto(order));
        order.setPaymentId(paymentDto.getPaymentId());

        // Set order status to ON_PAYMENT because payment has been initiated
        order.setState(OrderState.ON_PAYMENT);

        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto returnProduct(ProductReturnRequest productReturnRequest) {

        UUID orderId = productReturnRequest.getOrderId();
        Order order = getOrderById(orderId);

        if (order.getState() != OrderState.DELIVERED) {
            throw new ProductReturnException("Order has not been delivered, return is not possible");
        }

        Map<UUID, Long> productsToReturn = productReturnRequest.getProducts();
        Map<UUID, Long> orderProducts = order.getProducts();

        boolean isReturnExistsInOrder = orderProducts.keySet().containsAll(productsToReturn.keySet());
        if (!isReturnExistsInOrder) {
            throw new IllegalArgumentException(
                    "Some of the specified products for return are not present in the order."
            );
        }

        // Refund logic is not implemented yet.

        ProductReturnMapRequest returnMapRequest = ProductReturnMapRequest.builder()
                .products(productsToReturn)
                .build();
        warehouseClient.acceptReturn(returnMapRequest);
        order.setState(OrderState.PRODUCT_RETURNED);

        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto payment(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAID);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto paymentFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto delivery(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERED);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto deliveryFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto complete(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto calculateTotalCost(UUID orderId) {
        Order order = getOrderById(orderId);
        BigDecimal totalCost = paymentClient.calculateTotalCost(mapper.toDto(order));
        order.setTotalPrice(totalCost);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrderById(orderId);
        BigDecimal deliveryCost = deliveryClient.calculateDeliveryCost(mapper.toDto(order));
        order.setDeliveryPrice(deliveryCost);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto assembly(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLED);
        return mapper.toDto(orderRepository.save(order));
    }

    @Transactional
    @Override
    public OrderDto assemblyFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);
        return mapper.toDto(orderRepository.save(order));
    }

    private Order getOrderById(UUID orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new NoOrderFoundException("Order with id " + orderId + " was not found")
                );
    }
}
