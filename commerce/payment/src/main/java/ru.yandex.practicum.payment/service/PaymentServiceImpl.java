package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.client.OrderClient;
import ru.yandex.practicum.interaction.client.StoreClient;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.enums.PaymentState;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NotEnoughInfoInOrderToCalculateException;
import ru.yandex.practicum.interaction.exception.NotFoundException;
import ru.yandex.practicum.interaction.exception.ProductNotFoundException;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private static final BigDecimal FEE_RATE = BigDecimal.valueOf(0.10);

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final StoreClient storeClient;
    private final PaymentMapper mapper;

    @Transactional
    @Override
    public PaymentDto createPayment(OrderDto orderDto) {

        checkInfo(orderDto);
        if (orderDto.getTotalPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты необходима общая стоимость заказа");
        }

        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(orderDto.getTotalPrice())
                .deliveryTotal(orderDto.getDeliveryPrice())
                .productTotal(orderDto.getProductPrice())
                .feeTotal(
                        orderDto.getTotalPrice()
                                .subtract(orderDto.getDeliveryPrice())
                                .subtract(orderDto.getProductPrice())
                )
                .paymentState(PaymentState.PENDING)
                .build();

        return mapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        checkInfo(orderDto);
        BigDecimal fee = orderDto.getProductPrice().multiply(FEE_RATE);
        return fee.add(orderDto.getProductPrice()).add(orderDto.getDeliveryPrice());
    }

    @Transactional
    @Override
    public void markPaymentSuccess(UUID paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId не должен быть null");
        }
        Payment payment = findById(paymentId);
        payment.setPaymentState(PaymentState.SUCCESS);
        paymentRepository.save(payment);
        orderClient.payment(payment.getOrderId());
    }

    @Transactional
    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {

        Map<UUID, Long> products = orderDto.getProducts();
        if (products == null || products.isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Нет товаров для расчета их стоимости");
        }

        List<UUID> productIds = new ArrayList<>(products.keySet());
        List<ProductDto> productDtoList = storeClient.getProductListByIds(productIds);


        if (productDtoList.isEmpty()) {
            throw new ProductNotFoundException("Ни один из товаров не найден в магазине, невозможно рассчитать стоимость");
        }


        return productDtoList.stream()
                .map(productDto -> {
                    Long productQuantity = products.get(productDto.getProductId());
                    return productDto.getPrice().multiply(BigDecimal.valueOf(productQuantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transactional
    @Override
    public void markPaymentFailed(UUID paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("paymentId не должен быть null");
        }
        Payment payment = findById(paymentId);
        payment.setPaymentState(PaymentState.FAILED);
        paymentRepository.save(payment);
        orderClient.paymentFailed(payment.getOrderId());
    }

    private Payment findById(UUID paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new NotFoundException("Такого платежа нет в базе"));
    }

    private void checkInfo(OrderDto orderDto) {
        if (paymentRepository.existsByOrderId(orderDto.getOrderId())) {
            throw new ConflictException("Платеж для заказа с ID: " + orderDto.getOrderId() + " уже существует");
        }

        if (orderDto.getProducts() == null || orderDto.getProducts().isEmpty()) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты необходимо наличие товаров в заказе");
        }

        if (orderDto.getOrderId() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты необходим orderId");
        }

        if (orderDto.getDeliveryPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты необходима стоимость доставки");
        }
        if (orderDto.getProductPrice() == null) {
            throw new NotEnoughInfoInOrderToCalculateException("Для оплаты необходима стоимость товаров");
        }
    }
}
