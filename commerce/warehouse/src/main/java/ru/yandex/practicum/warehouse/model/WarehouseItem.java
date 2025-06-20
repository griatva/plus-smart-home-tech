package ru.yandex.practicum.warehouse.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "warehouse_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseItem {

    @Id
    @UuidGenerator
    @Column(name = "warehouse_item_id", nullable = false, updatable = false)
    private UUID warehouseItemId;

    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(name = "width", nullable = false)
    private Double width; // ширина

    @Column(name = "height", nullable = false)
    private Double height; // высота

    @Column(name = "depth", nullable = false)
    private Double depth; // глубина

    @Column(name = "weight", nullable = false)
    private Double weight; // вес

    @Column(name = "fragile")
    private Boolean fragile; // признак хрупкости

    @Column(name = "quantity", nullable = false)
    private long quantity = 0;

}
