package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.interaction.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.warehouse.exception.NoSpecifiedProductInWarehouseException;
import ru.yandex.practicum.warehouse.exception.NotFoundException;
import ru.yandex.practicum.warehouse.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.warehouse.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.mapper.WarehouseItemMapper;
import ru.yandex.practicum.warehouse.model.WarehouseItem;
import ru.yandex.practicum.warehouse.repository.WarehouseRepository;

import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private static final String[] ADDRESSES = {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS;

    static {
        Random rnd = new SecureRandom();
        CURRENT_ADDRESS = ADDRESSES[rnd.nextInt(2)];
    }

    private final WarehouseRepository repository;

    @Transactional
    @Override
    public void createNewItem(NewProductInWarehouseRequest request) {

        Optional<WarehouseItem> item = repository.findByProductId(request.getProductId());
        if (item.isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с таким id: {} уже существует в БД");
        }

        repository.save(WarehouseItemMapper.toNewEntity(request));
    }

    @Transactional
    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cartDto) {

        Map<UUID, Long> products = cartDto.getProducts();
        List<UUID> productIds = new ArrayList<>(products.keySet());
        List<WarehouseItem> items = repository.findAllByProductIdIn(productIds);

        if (items.size() < productIds.size()) {
            throw new NotFoundException("Не все товары найдены на складе");
        }

        checkProductQuantity(items, products);

        return getBookedProductsDto(items);
    }

    private static BookedProductsDto getBookedProductsDto(List<WarehouseItem> items) {
        double deliveryWeight = items.stream()
                .mapToDouble(WarehouseItem::getWeight)
                .sum();

        double deliveryVolume = items.stream()
                .mapToDouble(item -> item.getWidth() * item.getHeight() * item.getDepth()) // не ясно, какие единицы измерения
                .sum();

        boolean fragile = items.stream()
                .anyMatch(item -> Boolean.TRUE.equals(item.getFragile()));


        return BookedProductsDto.builder()
                .deliveryWeight(deliveryWeight)
                .deliveryVolume(deliveryVolume)
                .fragile(fragile)
                .build();
    }

    private static void checkProductQuantity(List<WarehouseItem> items, Map<UUID, Long> products) {
        Map<UUID, Long> lowQuantityProducts = new HashMap<>(); // Long - реально оставшееся количество

        for (WarehouseItem item : items) {
            UUID productId = item.getProductId();
            Long quantity = item.getQuantity();

            Long cartQuantity = products.get(productId);

            if (cartQuantity > quantity) {
                lowQuantityProducts.put(productId, quantity);
            }
        }

        if (!lowQuantityProducts.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("На складе недостаточно товаров:\n");
            lowQuantityProducts.forEach((productId, quantity) ->
                    errorMessage.append("Продукт ID: ").append(productId)
                            .append(" — остаток на складе: ").append(quantity)
                            .append("\n")
            );
            throw new ProductInShoppingCartLowQuantityInWarehouse(errorMessage.toString());
        }
    }

    @Transactional
    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        WarehouseItem item = repository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("Нет информации о товаре на складе"));

        item.setQuantity(item.getQuantity() + request.getQuantity());

        repository.save(item);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return new AddressDto(
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS,
                CURRENT_ADDRESS
        );
    }
}
