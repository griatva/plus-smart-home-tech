package ru.yandex.practicum.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.mapper.CartMapper;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.cart.repository.CartRepository;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.exception.ConflictException;
import ru.yandex.practicum.interaction.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.interaction.exception.NotAuthorizedUserException;
import ru.yandex.practicum.interaction.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final WarehouseClient warehouseClient;
    private final CartRepository repository;

    @Transactional(readOnly = true)
    @Override
    public ShoppingCartDto getCart(String userName) {
        // User authorization (currently only checks that username is not blank)
        checkUserName(userName);
        ShoppingCart cart = getCartByUserName(userName);

        return CartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto putProductsToCart(String userName, Map<UUID, Long> products) {
        // User authorization (currently only checks that username is not blank)
        checkUserName(userName);

        ShoppingCart cart = repository.findByUserName(userName).orElse(null);

        if (cart == null) {
            cart = ShoppingCart.builder()
                    .userName(userName)
                    .state(true)
                    .products(new HashMap<>())
                    .build();
        } else {
            if (!cart.isState()) {
                throw new ConflictException("The shopping cart is deactivated. Editing is not allowed.");
            }
        }

        Map<UUID, Long> requestedProducts = new HashMap<>(cart.getProducts());
        products.forEach((productId, quantity) ->
                requestedProducts.merge(productId, quantity, Long::sum)
        );

        checkQuantityInWarehouse(requestedProducts, cart.getShoppingCartId());

        cart.getProducts().putAll(requestedProducts);

        ShoppingCart savedCart = repository.save(cart);
        return CartMapper.toDto(savedCart);
    }

    private void checkQuantityInWarehouse(Map<UUID, Long> requestedProducts, UUID cartId) {
        ShoppingCartDto requestToWarehouse = ShoppingCartDto.builder()
                .shoppingCartId(cartId)
                .products(requestedProducts)
                .build();

        BookedProductsDto booked = warehouseClient.checkQuantity(requestToWarehouse);
        log.info(
                "Reservation completed successfully: volume = {}, weight = {}, fragile = {}",
                booked.getDeliveryVolume(),
                booked.getDeliveryWeight(),
                booked.getFragile()
        );
    }

    @Transactional
    @Override
    public void deactivateCart(String userName) {
        // User authorization (currently only checks that username is not blank)
        checkUserName(userName);
        ShoppingCart cart = getCartByUserName(userName);

        cart.setState(false);
        repository.save(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto removeProductFromCart(String userName, List<UUID> productIds) {

        checkUserName(userName);
        ShoppingCart cart = getCartByUserName(userName);

        if (!cart.isState()) {
            throw new ConflictException("The shopping cart is deactivated. Editing is not allowed.");
        }

        boolean allExist = productIds.stream()
                .allMatch(productId -> cart.getProducts().containsKey(productId));

        if (!allExist) {
            throw new NoProductsInShoppingCartException(
                    "Some products were not found in the shopping cart: " + productIds
            );
        }

        productIds.forEach(cart.getProducts()::remove);

        ShoppingCart savedCart = repository.save(cart);
        return CartMapper.toDto(savedCart);
    }

    @Transactional
    @Override
    public ShoppingCartDto changeQuantityInCart(String userName, ChangeProductQuantityRequest request) {

        checkUserName(userName);
        ShoppingCart cart = getCartByUserName(userName);

        if (!cart.isState()) {
            throw new ConflictException("The shopping cart is deactivated. Editing is not allowed.");
        }

        UUID productId = request.getProductId();
        Long newQuantity = request.getNewQuantity();

        if (!cart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException(
                    "The requested product was not found in the shopping cart: productId = " + productId
            );
        }

        Map<UUID, Long> updatedProducts = new HashMap<>(cart.getProducts());
        updatedProducts.put(productId, newQuantity);

        checkQuantityInWarehouse(updatedProducts, cart.getShoppingCartId());

        cart.getProducts().put(productId, newQuantity);

        ShoppingCart savedCart = repository.save(cart);
        return CartMapper.toDto(savedCart);
    }

    private ShoppingCart getCartByUserName(String userName) {
        return repository.findByUserName(userName)
                .orElseThrow(() ->
                        new NotFoundException("Shopping cart for user " + userName + " was not found")
                );
    }

    private void checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Username must not be empty");
        }
    }
}
