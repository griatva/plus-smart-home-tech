package ru.yandex.practicum.cart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "shopping_cart")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShoppingCart {

    @Id
    @UuidGenerator
    @Column(name = "shopping_cart_id", nullable = false, updatable = false)
    private UUID shoppingCartId;

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;

    @Column(name = "state", nullable = false)
    private boolean state;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shopping_cart_products", joinColumns = @JoinColumn(name = "shopping_cart_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Long> products; // productId -> quantity (сколько хочет покупатель)

}
