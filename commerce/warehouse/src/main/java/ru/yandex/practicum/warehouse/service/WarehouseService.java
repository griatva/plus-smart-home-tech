package ru.yandex.practicum.warehouse.service;

import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.*;

public interface WarehouseService {

    void createNewItem(NewProductInWarehouseRequest request);

    BookedProductsDto checkQuantity(ShoppingCartDto cartDto);

    void addProduct(AddProductToWarehouseRequest request);

    AddressDto getWarehouseAddress();

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(ProductReturnMapRequest request);

    BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request);

}
