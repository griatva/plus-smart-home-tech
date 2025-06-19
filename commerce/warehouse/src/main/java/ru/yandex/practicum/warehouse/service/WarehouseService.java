package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;

public interface WarehouseService {

    void createNewItem(NewProductInWarehouseRequest request);

    BookedProductsDto checkQuantity(ShoppingCartDto cartDto);

    void addProduct(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();
}
