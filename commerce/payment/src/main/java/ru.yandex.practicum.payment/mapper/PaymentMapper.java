package ru.yandex.practicum.payment.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.model.Payment;

@Component
public class PaymentMapper {

    public PaymentDto toDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrderId())
                .totalPayment(payment.getTotalPayment())
                .deliveryTotal(payment.getDeliveryTotal())
                .feeTotal(payment.getFeeTotal())
                .productTotal(payment.getProductTotal())
                .paymentState(payment.getPaymentState())
                .build();
    }

    public Payment toEntity(PaymentDto dto) {
        if (dto == null) {
            return null;
        }

        return Payment.builder()
                .paymentId(dto.getPaymentId())
                .orderId(dto.getOrderId())
                .totalPayment(dto.getTotalPayment())
                .deliveryTotal(dto.getDeliveryTotal())
                .feeTotal(dto.getFeeTotal())
                .productTotal(dto.getProductTotal())
                .paymentState(dto.getPaymentState())
                .build();
    }

}
