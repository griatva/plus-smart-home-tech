package ru.yandex.practicum.interaction.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.*;

@Component
public class WarehouseFallback implements WarehouseClient {

    @Override
    public void createNewItem(NewProductInWarehouseRequest request) {
        System.out.println("Fallback: createNewItem — склад недоступен, товар не добавлен.");
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cartDto) {
        System.out.println("Fallback: checkQuantity — склад недоступен, возвращаем пустые параметры доставки.");

        return BookedProductsDto.builder().build();
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        System.out.println("Fallback: addProduct — склад недоступен, товар не добавлен.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        System.out.println("Fallback: getWarehouseAddress — склад недоступен, возвращаем пустой адрес.");

        return AddressDto.builder().build();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        System.out.println("Fallback: shippedToDelivery — склад недоступен, товары не переданы в доставку.");
    }

    @Override
    public void acceptReturn(ProductReturnMapRequest request) {
        System.out.println("Fallback: acceptReturn — склад недоступен, возврат не принят.");
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        System.out.println("Fallback: assemblyProductsForOrder — склад недоступен, возвращаем пустые параметры доставки.");
        return BookedProductsDto.builder().build();
    }
}
