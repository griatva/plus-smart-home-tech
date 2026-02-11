package ru.yandex.practicum.interaction.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.PaymentClient;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class PaymentFallback implements PaymentClient {

    @Override
    public PaymentDto createPayment(OrderDto orderDto) {
        System.out.println("Fallback: createPayment — payment service is unavailable, payment was not created.");
        return emptyPaymentDto();
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        System.out.println("Fallback: calculateTotalCost — payment service is unavailable, returning 0.");
        return BigDecimal.ZERO;
    }

    @Override
    public void markPaymentSuccess(UUID paymentId) {
        System.out.println("Fallback: markPaymentSuccess — payment service is unavailable, payment success was not recorded.");
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        System.out.println("Fallback: calculateProductCost — payment service is unavailable, returning 0.");
        return BigDecimal.ZERO;
    }

    @Override
    public void markPaymentFailed(UUID paymentId) {
        System.out.println("Fallback: markPaymentFailed — payment service is unavailable, payment failure was not recorded.");
    }

    private PaymentDto emptyPaymentDto() {
        return PaymentDto.builder().build();
    }
}