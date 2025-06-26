package ru.yandex.practicum.interaction.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;

@Component
public class WarehouseFallback implements WarehouseClient {

    @Override
    public void createNewItem(NewProductInWarehouseRequest request) {
        System.out.println("Fallback: createNewItem — склад недоступен, товар не добавлен.");
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cartDto) {
        System.out.println("Fallback: checkQuantity — склад недоступен, возвращаем пустые параметры доставки.");

        return BookedProductsDto.builder()
                .deliveryWeight(0.0)
                .deliveryVolume(0.0)
                .fragile(false)
                .build();
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        System.out.println("Fallback: addProduct — склад недоступен, товар не добавлен.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        System.out.println("Fallback: getWarehouseAddress — склад недоступен, возвращаем фиктивный адрес.");

        return AddressDto.builder()
                .country("Неизвестно")
                .city("Неизвестно")
                .street("Неизвестно")
                .house("Неизвестно")
                .flat("Неизвестно")
                .build();
    }
}
