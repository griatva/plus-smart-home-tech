package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentDto> createPayment(@RequestBody @Valid OrderDto orderDto) {
        log.info("Received request to create payment for order: {}", orderDto);
        return ResponseEntity.ok(paymentService.createPayment(orderDto));
    }

    @PostMapping("/totalCost")
    public ResponseEntity<BigDecimal> calculateTotalCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Received request to calculate total cost for order: {}", orderDto);
        return ResponseEntity.ok(paymentService.calculateTotalCost(orderDto));
    }

    @PostMapping("/refund")
    public ResponseEntity<Void> markPaymentSuccess(@RequestBody UUID paymentId) {
        log.info("Received request to mark payment as successful, paymentId = {}", paymentId);
        paymentService.markPaymentSuccess(paymentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/productCost")
    public ResponseEntity<BigDecimal> calculateProductCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Received request to calculate product cost for order: {}", orderDto);
        return ResponseEntity.ok(paymentService.calculateProductCost(orderDto));
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> markPaymentFailed(@RequestBody UUID paymentId) {
        log.info("Received request to mark payment as failed, paymentId = {}", paymentId);
        paymentService.markPaymentFailed(paymentId);
        return ResponseEntity.ok().build();
    }
}