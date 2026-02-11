package ru.yandex.practicum.warehouse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.*;
import ru.yandex.practicum.interaction.exception.*;
import ru.yandex.practicum.warehouse.mapper.WarehouseItemMapper;
import ru.yandex.practicum.warehouse.model.OrderBooking;
import ru.yandex.practicum.warehouse.model.WarehouseItem;
import ru.yandex.practicum.warehouse.repository.BookingRepository;
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

    private final WarehouseRepository warehouseRepository;
    private final BookingRepository bookingRepository;


    @Transactional
    @Override
    public void createNewItem(NewProductInWarehouseRequest request) {

        Optional<WarehouseItem> item = warehouseRepository.findByProductId(request.getProductId());
        if (item.isPresent()) {
            throw new SpecifiedProductAlreadyInWarehouseException(
                    "Product with id: " + request.getProductId() + " already exists in the database");
        }

        warehouseRepository.save(WarehouseItemMapper.toNewEntity(request));
    }

    @Transactional
    @Override
    public BookedProductsDto checkQuantity(ShoppingCartDto cartDto) {

        Map<UUID, Long> products = cartDto.getProducts();
        List<UUID> productIds = new ArrayList<>(products.keySet());
        List<WarehouseItem> items = warehouseRepository.findAllByProductIdIn(productIds);

        if (items.size() < productIds.size()) {
            throw new ProductInShoppingCartNotInWarehouse("Not all products were found in the warehouse");
        }

        checkProductQuantity(items, products);

        return getBookedProductsDto(items);
    }

    private static BookedProductsDto getBookedProductsDto(List<WarehouseItem> items) {
        double deliveryWeight = items.stream()
                .mapToDouble(WarehouseItem::getWeight)
                .sum();

        double deliveryVolume = items.stream()
                .mapToDouble(item -> item.getWidth() * item.getHeight() * item.getDepth()) // clarify measurement units
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
        Map<UUID, Long> lowQuantityProducts = new HashMap<>(); // Long - actual remaining quantity in stock

        for (WarehouseItem item : items) {
            UUID productId = item.getProductId();
            Long quantity = item.getQuantity();

            Long cartQuantity = products.get(productId);

            if (cartQuantity > quantity) {
                lowQuantityProducts.put(productId, quantity);
            }
        }

        if (!lowQuantityProducts.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Not enough products in stock:\n");
            lowQuantityProducts.forEach((productId, quantity) ->
                    errorMessage.append("Product ID: ").append(productId)
                            .append(" — available in stock: ").append(quantity)
                            .append("\n")
            );
            throw new ProductInShoppingCartLowQuantityInWarehouse(errorMessage.toString());
        }
    }

    @Transactional
    @Override
    public void addProduct(AddProductToWarehouseRequest request) {
        WarehouseItem item = warehouseRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new NoSpecifiedProductInWarehouseException("No information about the product in the warehouse"));

        item.setQuantity(item.getQuantity() + request.getQuantity());

        warehouseRepository.save(item);
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

    @Transactional
    @Override
    public void shippedToDelivery(ShippedToDeliveryRequest request) {

        OrderBooking orderBooking = bookingRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new NotFoundException("Booking for this order was not found"));

        orderBooking.setDeliveryId(request.getDeliveryId());
        bookingRepository.save(orderBooking);
    }

    @Transactional
    @Override
    public void acceptReturn(ProductReturnMapRequest request) {

        Map<UUID, Long> productsForReturn = request.getProducts();

        if (productsForReturn == null || productsForReturn.isEmpty()) {
            throw new IllegalArgumentException("No products provided for return");
        }

        List<WarehouseItem> items = warehouseRepository.findAllByProductIdIn(new ArrayList<>(productsForReturn.keySet()));

        if (items.size() != productsForReturn.size()) {
            throw new IllegalArgumentException("Some returned products were not found in the warehouse database.");
        }

        for (WarehouseItem item : items) {
            Long returnQuantity = productsForReturn.get(item.getProductId());
            if (returnQuantity == null) {
                continue;
            }
            item.setQuantity(item.getQuantity() + returnQuantity);
        }
        warehouseRepository.saveAll(items);
    }


    @Transactional
    @Override
    public BookedProductsDto assemblyProductsForOrder(AssemblyProductsForOrderRequest request) {

        Map<UUID, Long> products = request.getProducts();
        List<UUID> productIds = new ArrayList<>(products.keySet());
        List<WarehouseItem> items = warehouseRepository.findAllByProductIdIn(productIds);

        if (items.size() < productIds.size()) {
            throw new NotFoundException("Not all products were found in the warehouse");
        }

        // Check available stock quantities
        checkProductQuantity(items, products);

        // Decrease stock quantities
        for (WarehouseItem item : items) {
            Long bookQuantity = products.get(item.getProductId());
            if (bookQuantity == null) {
                continue;
            }
            item.setQuantity(item.getQuantity() - bookQuantity);
        }
        warehouseRepository.saveAll(items);


        // Create and save a booking
        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(request.getOrderId())
                .products(request.getProducts())
                .build();
        bookingRepository.save(orderBooking);

        return getBookedProductsDto(items);
    }
}
