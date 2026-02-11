package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.order.ProductReturnRequest;
import ru.yandex.practicum.order.service.OrderService;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService service;

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getClientOrders(@RequestParam String username,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        log.info("Received request to get orders for user: {}", username);
        return ResponseEntity.ok(service.getClientOrdersPaginated(username, page, size));
    }

    @PutMapping
    public ResponseEntity<OrderDto> createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        log.info("Received request to create a new order: {}", request);
        return ResponseEntity.ok(service.createNewOrder(request));
    }

    @PostMapping("/return")
    public ResponseEntity<OrderDto> productReturn(@RequestBody @Valid ProductReturnRequest productReturnRequest) {
        log.info("Received request to return products for an order: {}", productReturnRequest);
        return ResponseEntity.ok(service.returnProduct(productReturnRequest));
    }

    @PostMapping("/payment")
    public ResponseEntity<OrderDto> payment(@RequestBody UUID orderId) {
        log.info("Received request to process payment for orderId = {}", orderId);
        return ResponseEntity.ok(service.payment(orderId));
    }

    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId) {
        log.info("Received request to mark payment failure for orderId = {}", orderId);
        return ResponseEntity.ok(service.paymentFailed(orderId));
    }

    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> delivery(@RequestBody UUID orderId) {
        log.info("Received request to start delivery for orderId = {}", orderId);
        return ResponseEntity.ok(service.delivery(orderId));
    }

    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> deliveryFailed(@RequestBody UUID orderId) {
        log.info("Received request to mark delivery failure for orderId = {}", orderId);
        OrderDto response = service.deliveryFailed(orderId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/completed")
    public ResponseEntity<OrderDto> complete(@RequestBody UUID orderId) {
        log.info("Received request to complete order with orderId = {}", orderId);
        return ResponseEntity.ok(service.complete(orderId));
    }

    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalCost(@RequestBody UUID orderId) {
        log.info("Received request to calculate total cost for orderId = {}", orderId);
        return ResponseEntity.ok(service.calculateTotalCost(orderId));
    }

    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody UUID orderId) {
        log.info("Received request to calculate delivery cost for orderId = {}", orderId);
        return ResponseEntity.ok(service.calculateDeliveryCost(orderId));
    }

    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> assembly(@RequestBody UUID orderId) {
        log.info("Received request to assemble order with orderId = {}", orderId);
        return ResponseEntity.ok(service.assembly(orderId));
    }

    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> assemblyFailed(@RequestBody UUID orderId) {
        log.info("Received request to mark assembly failure for orderId = {}", orderId);
        return ResponseEntity.ok(service.assemblyFailed(orderId));
    }
}
