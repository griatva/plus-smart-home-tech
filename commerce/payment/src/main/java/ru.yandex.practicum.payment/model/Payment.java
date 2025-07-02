package ru.yandex.practicum.payment.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @UuidGenerator
    @Column(name = "payment_id", nullable = false, updatable = false)
    private UUID paymentId;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "total_payment", nullable = false)
    private BigDecimal totalPayment;

    @Column(name = "delivery_total", nullable = false)
    private BigDecimal deliveryTotal;

    @Column(name = "fee_total", nullable = false)
    private BigDecimal feeTotal;

    @Column(name = "product_total", nullable = false)
    private BigDecimal productTotal;

    @Column(name = "payment_state", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentState paymentState = PaymentState.PENDING;

}
