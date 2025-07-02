package ru.yandex.practicum.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.config.FeignConfig;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.fallback.PaymentFallback;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "payment",
        path = "/api/v1/payment",
        configuration = FeignConfig.class,
        fallback = PaymentFallback.class)
public interface PaymentClient {

    @PostMapping
    PaymentDto createPayment(@RequestBody OrderDto orderDto);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCost(@RequestBody OrderDto orderDto);

    @PostMapping("/refund")
    void markPaymentSuccess(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal calculateProductCost(@RequestBody OrderDto orderDto);

    @PostMapping("/failed")
    void markPaymentFailed(@RequestBody UUID paymentId);
}