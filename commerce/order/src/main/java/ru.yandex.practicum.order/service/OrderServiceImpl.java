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
        //авторизация пользователя (пока что проверяем, что имя не пустое) - 401 NotAuthorizedUserException
        checkUserName(username);
        Pageable pageRequest = PageRequest.of(page, size);

        ShoppingCartDto cart = cartClient.getCart(username);
        Page<Order> ordersPage = orderRepository.findAllByShoppingCartId(cart.getShoppingCartId(), pageRequest);
        return ordersPage.map(mapper::toDto);
    }

    private void checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым!");
        }
    }


    @Transactional
    @Override
    public OrderDto createNewOrder(CreateNewOrderRequest request) {

        //создаем новый заказ, заполняем уже имеющимися данными: cartId, products, state
        ShoppingCartDto shoppingCart = request.getShoppingCart();
        AddressDto deliveryAddress = request.getDeliveryAddress();

        if (shoppingCart.getProducts() == null || shoppingCart.getProducts().isEmpty()) {
            throw new IllegalArgumentException("Нельзя создать заказ: корзина пуста.");
        }

        if (deliveryAddress == null) {
            throw new IllegalArgumentException("Адрес доставки не указан.");
        }

        Order order = Order.builder()
                .shoppingCartId(shoppingCart.getShoppingCartId())
                .products(shoppingCart.getProducts())
                .state(OrderState.NEW)
                .build();

        order = orderRepository.save(order);
        UUID orderId = order.getOrderId();

        //собираем заказ на складе, получаем вес, объем, хрупкость
        AssemblyProductsForOrderRequest bookRequest = AssemblyProductsForOrderRequest.builder()
                .orderId(orderId)
                .products(shoppingCart.getProducts())
                .build();
        BookedProductsDto bookedProductsDto = warehouseClient.assemblyProductsForOrder(bookRequest);

        order.setDeliveryWeight(bookedProductsDto.getDeliveryWeight());
        order.setDeliveryVolume(bookedProductsDto.getDeliveryVolume());
        order.setFragile(bookedProductsDto.getFragile());

        //получаем со склада адрес
        AddressDto fromAddress = warehouseClient.getWarehouseAddress();

        //создаем доставку и добавляем ее id в order
        DeliveryDto deliveryDtoToPlan = DeliveryDto.builder()
                .fromAddress(fromAddress)
                .toAddress(deliveryAddress)
                .orderId(orderId)
                .deliveryState(DeliveryState.CREATED)
                .build();

        DeliveryDto deliveryDtoCreated = deliveryClient.planDelivery(deliveryDtoToPlan);

        order.setDeliveryId(deliveryDtoCreated.getDeliveryId());

        //отправляем запрос в деливери, чтобы получить стоимость доставки
        BigDecimal deliveryCost = deliveryClient.calculateDeliveryCost(mapper.toDto(order));

        order.setDeliveryPrice(deliveryCost);

        //отправляем запрос в payment, чтобы получить стоимость товаров и общую стоимость
        BigDecimal productCost = paymentClient.calculateProductCost(mapper.toDto(order));
        order.setProductPrice(productCost);

        BigDecimal totalCost = paymentClient.calculateTotalCost(mapper.toDto(order));
        order.setTotalPrice(totalCost);

        //создаем payment, получаем его id
        PaymentDto paymentDto = paymentClient.createPayment(mapper.toDto(order));
        order.setPaymentId(paymentDto.getPaymentId());

        //устанавливаем статус заказа в состояние ON_PAYMENT, т.к. мы "запустили" оплату, создав payment
        order.setState(OrderState.ON_PAYMENT);

        return mapper.toDto(orderRepository.save(order));
    }


    @Transactional
    @Override
    public OrderDto returnProduct(ProductReturnRequest productReturnRequest) {

        UUID orderId = productReturnRequest.getOrderId();

        Order order = getOrderById(orderId);

        if (order.getState() != OrderState.DELIVERED) {
            throw new ProductReturnException("Заказ не доставлен, возврат невозможен");
        }

        Map<UUID, Long> productsToReturn = productReturnRequest.getProducts();
        Map<UUID, Long> orderProducts = order.getProducts();

        boolean isReturnExistsInOrder = orderProducts.keySet().containsAll(productsToReturn.keySet());
        if (!isReturnExistsInOrder) {
            throw new IllegalArgumentException("В заказе отсутствуют некоторые из указанных товаров для возврата.");
        }

        //логика по возврату денег не зафиксирована.

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
                .orElseThrow(() -> new NoOrderFoundException("Заказ с ID " + orderId + " не найден"));
    }
}
