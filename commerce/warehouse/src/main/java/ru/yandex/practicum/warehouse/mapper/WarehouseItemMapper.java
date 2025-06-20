package ru.yandex.practicum.warehouse.mapper;

import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.model.WarehouseItem;

public class WarehouseItemMapper {


    public static WarehouseItem toNewEntity(NewProductInWarehouseRequest request) {
        if (request == null) return null;

        return WarehouseItem.builder()
                .productId(request.getProductId())
                .width(request.getDimension().getWidth())
                .height(request.getDimension().getHeight())
                .depth(request.getDimension().getDepth())
                .weight(request.getWeight())
                .fragile(request.getFragile())
                .build();
    }
}
