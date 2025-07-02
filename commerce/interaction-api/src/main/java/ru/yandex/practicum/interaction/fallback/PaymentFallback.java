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
        System.out.println("Fallback: createPayment — сервис оплаты недоступен, платеж не создан.");
        return emptyPaymentDto();
    }

    @Override
    public BigDecimal calculateTotalCost(OrderDto orderDto) {
        System.out.println("Fallback: calculateTotalCost — сервис оплаты недоступен, возвращаем 0.");
        return BigDecimal.ZERO;
    }

    @Override
    public void markPaymentSuccess(UUID paymentId) {
        System.out.println("Fallback: markPaymentSuccess — сервис оплаты недоступен, успех оплаты не зафиксирован.");
    }

    @Override
    public BigDecimal calculateProductCost(OrderDto orderDto) {
        System.out.println("Fallback: calculateProductCost — сервис оплаты недоступен, возвращаем 0.");
        return BigDecimal.ZERO;
    }

    @Override
    public void markPaymentFailed(UUID paymentId) {
        System.out.println("Fallback: markPaymentFailed — сервис оплаты недоступен, ошибка оплаты не зафиксирована.");
    }

    private PaymentDto emptyPaymentDto() {
        return PaymentDto.builder().build();
    }
}