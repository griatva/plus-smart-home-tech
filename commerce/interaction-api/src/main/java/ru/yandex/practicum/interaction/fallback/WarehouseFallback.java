package ru.yandex.practicum.interaction.fallback;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.*;

@Component
public class WarehouseFallback implements WarehouseClient {

    @Override
    public void createNewItem(NewProductInWarehouseRequest request) {
        System.out.println("Fallback: createNewItem — warehouse service is unavailable, product was not added.");
    }

    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cartDto) {
        System.out.println("Fallback: checkQuantity — warehouse service is unavailable, returning empty delivery parameters.");
        return BookedProductsDto.builder().build();
    }

    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        System.out.println("Fallback: addProduct — warehouse service is unavailable, product was not added.");
    }

    @Override
    public AddressDto getWarehouseAddress() {
        System.out.println("Fallback: getWarehouseAddress — warehouse service is unavailable, returning an empty address.");
        return AddressDto.builder().build();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {
        System.out.println("Fallback: shippedToDelivery — warehouse service is unavailable, products were not handed over for delivery.");
    }

    @Override
    public void acceptReturn(ProductReturnMapRequest request) {
        System.out.println("Fallback: acceptReturn — warehouse service is unavailable, return was not accepted.");
    }

    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {
        System.out.println("Fallback: assemblyProductsForOrder — warehouse service is unavailable, returning empty delivery parameters.");
        return BookedProductsDto.builder().build();
    }
}
