package ru.yandex.practicum.delivery.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction.enums.DeliveryState;

import java.util.UUID;

@Data
@Entity
@Table(name = "delivery")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {

    @Id
    @UuidGenerator
    @Column(name = "delivery_id", nullable = false, updatable = false)
    private UUID deliveryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_address_id", referencedColumnName = "address_id", nullable = false)
    private Address fromAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_address_id", referencedColumnName = "address_id", nullable = false)
    private Address toAddress;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_State", nullable = false)
    private DeliveryState deliveryState;
}
