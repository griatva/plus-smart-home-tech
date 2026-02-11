package ru.yandex.practicum.payment.service;

import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    PaymentDto createPayment(OrderDto orderDto);

    BigDecimal calculateTotalCost(OrderDto orderDto);

    void markPaymentSuccess(UUID paymentId);

    BigDecimal calculateProductCost(OrderDto orderDto);

    void markPaymentFailed(UUID paymentId);

}
