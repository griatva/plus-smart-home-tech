package ru.yandex.practicum.delivery.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.interaction.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/delivery")
public class DeliveryController {

    private final DeliveryService service;

    @PutMapping
    public ResponseEntity<DeliveryDto> planDelivery(@RequestBody @Valid DeliveryDto deliveryDto) {
        log.info("Received request to plan delivery: {}", deliveryDto);
        return ResponseEntity.ok(service.planDelivery(deliveryDto));
    }

    @PostMapping("/successful")
    public ResponseEntity<Void> deliverySuccessful(@RequestBody UUID orderId) {
        log.info("Received request to emulate successful delivery for orderId = {}", orderId);
        service.markDeliverySuccessful(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/picked")
    public ResponseEntity<Void> deliveryPicked(@RequestBody UUID orderId) {
        log.info("Received request to emulate order pickup for delivery, orderId = {}", orderId);
        service.markDeliveryPicked(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/failed")
    public ResponseEntity<Void> deliveryFailed(@RequestBody UUID orderId) {
        log.info("Received request to emulate failed delivery for orderId = {}", orderId);
        service.markDeliveryFailed(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cost")
    public ResponseEntity<BigDecimal> calculateDeliveryCost(@RequestBody @Valid OrderDto orderDto) {
        log.info("Received request to calculate delivery cost for order: {}", orderDto);
        return ResponseEntity.ok(service.calculateDeliveryCost(orderDto));
    }
}
