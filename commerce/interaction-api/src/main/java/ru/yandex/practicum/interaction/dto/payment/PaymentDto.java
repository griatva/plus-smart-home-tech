package ru.yandex.practicum.interaction.dto.payment;


import lombok.*;
import ru.yandex.practicum.interaction.enums.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDto {

    private UUID paymentId;

    private UUID orderId;

    private BigDecimal totalPayment;

    private BigDecimal deliveryTotal;

    private BigDecimal feeTotal;

    private BigDecimal productTotal;

    private PaymentState paymentState;

}
