package ru.yandex.practicum.warehouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.model.WarehouseItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface WarehouseRepository extends JpaRepository<WarehouseItem, UUID> {

    Optional<WarehouseItem> findByProductId(UUID productId);

    List<WarehouseItem> findAllByProductIdIn(List<UUID> productIds);

}
