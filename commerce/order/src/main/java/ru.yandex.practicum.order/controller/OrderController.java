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
        log.info("Получен запрос на получение заказов пользователя: {}", username);
        return ResponseEntity.ok(service.getClientOrdersPaginated(username, page, size));
    }


    @PutMapping
    public ResponseEntity<OrderDto> createNewOrder(@RequestBody @Valid CreateNewOrderRequest request) {
        log.info("Получен запрос на создание нового заказа: {}", request);
        return ResponseEntity.ok(service.createNewOrder(request));
    }


    @PostMapping("/return")
    public ResponseEntity<OrderDto> productReturn(@RequestBody @Valid ProductReturnRequest productReturnRequest) {
        log.info("Получен запрос на возврат заказа: {}", productReturnRequest);
        return ResponseEntity.ok(service.returnProduct(productReturnRequest));
    }


    @PostMapping("/payment")
    public ResponseEntity<OrderDto> payment(@RequestBody UUID orderId) {
        log.info("Получен запрос на оплату заказа: {}", orderId);
        return ResponseEntity.ok(service.payment(orderId));
    }


    @PostMapping("/payment/failed")
    public ResponseEntity<OrderDto> paymentFailed(@RequestBody UUID orderId) {
        log.info("Получен запрос на отметку ошибки оплаты заказа: {}", orderId);
        return ResponseEntity.ok(service.paymentFailed(orderId));
    }


    @PostMapping("/delivery")
    public ResponseEntity<OrderDto> delivery(@RequestBody UUID orderId) {
        log.info("Получен запрос на доставку заказа: {}", orderId);
        return ResponseEntity.ok(service.delivery(orderId));
    }


    @PostMapping("/delivery/failed")
    public ResponseEntity<OrderDto> deliveryFailed(@RequestBody UUID orderId) {
        log.info("Получен запрос на ошибку доставки заказа: {}", orderId);
        OrderDto response = service.deliveryFailed(orderId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/completed")
    public ResponseEntity<OrderDto> complete(@RequestBody UUID orderId) {
        log.info("Получен запрос на завершение заказа: {}", orderId);
        return ResponseEntity.ok(service.complete(orderId));
    }


    @PostMapping("/calculate/total")
    public ResponseEntity<OrderDto> calculateTotalCost(@RequestBody UUID orderId) {
        log.info("Получен запрос на расчёт стоимости заказа: {}", orderId);
        return ResponseEntity.ok(service.calculateTotalCost(orderId));
    }


    @PostMapping("/calculate/delivery")
    public ResponseEntity<OrderDto> calculateDeliveryCost(@RequestBody UUID orderId) {
        log.info("Получен запрос на расчёт стоимости доставки заказа: {}", orderId);
        return ResponseEntity.ok(service.calculateDeliveryCost(orderId));
    }


    @PostMapping("/assembly")
    public ResponseEntity<OrderDto> assembly(@RequestBody UUID orderId) {
        log.info("Получен запрос на сборку заказа: {}", orderId);
        return ResponseEntity.ok(service.assembly(orderId));
    }

    @PostMapping("/assembly/failed")
    public ResponseEntity<OrderDto> assemblyFailed(@RequestBody UUID orderId) {
        log.info("Получен запрос на ошибку сборки заказа: {}", orderId);
        return ResponseEntity.ok(service.assemblyFailed(orderId));
    }
}

