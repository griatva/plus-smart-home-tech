package ru.yandex.practicum.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.cart.exception.ConflictException;
import ru.yandex.practicum.cart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.cart.exception.NotAuthorizedUserException;
import ru.yandex.practicum.cart.exception.NotFoundException;
import ru.yandex.practicum.cart.mapper.CartMapper;
import ru.yandex.practicum.cart.model.ShoppingCart;
import ru.yandex.practicum.cart.repository.CartRepository;
import ru.yandex.practicum.interaction.client.WarehouseClient;
import ru.yandex.practicum.interaction.dto.cart.ChangeProductQuantityRequest;
import ru.yandex.practicum.interaction.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.dto.warehouse.BookedProductsDto;

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
        //авторизация пользователя (пока что проверяем, что имя не пустое) - 401 NotAuthorizedUserException
        checkUserName(userName);
        ShoppingCart cart = getCartByUserName(userName);

        return CartMapper.toDto(cart);
    }

    @Transactional
    @Override
    public ShoppingCartDto putProductsToCart(String userName, Map<UUID, Long> products) {
        //авторизация пользователя (пока что проверяем, что имя не пустое) - 401 NotAuthorizedUserException
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
                throw new ConflictException("Корзина деактивирована. Редактирование запрещено.");
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
        ShoppingCartDto requestToWareHouse = ShoppingCartDto.builder()
                .shoppingCartId(cartId)
                .products(requestedProducts)
                .build();

        BookedProductsDto booked = warehouseClient.checkQuantity(requestToWareHouse);
        log.info("Бронирование прошло успешно: объём = {}, вес = {}, хрупкое = {}",
                booked.getDeliveryVolume(), booked.getDeliveryWeight(), booked.getFragile());
    }

    @Transactional
    @Override
    public void deactivateCart(String userName) {
        //авторизация пользователя (пока что проверяем, что имя не пустое) - 401 NotAuthorizedUserException
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
            throw new ConflictException("Корзина деактивирована. Редактирование запрещено.");
        }

        boolean allExist = productIds.stream()
                .allMatch(productId -> cart.getProducts().containsKey(productId));

        if (!allExist) {
            throw new NoProductsInShoppingCartException("Некоторые товары не найдены в корзине: " + productIds);
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
            throw new ConflictException("Корзина деактивирована. Редактирование запрещено.");
        }

        UUID productId = request.getProductId();
        Long newQuantity = request.getNewQuantity();

        if (!cart.getProducts().containsKey(productId)) {
            throw new NoProductsInShoppingCartException("Нет искомых товаров в корзине: productId = " + productId);
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
                .orElseThrow(() -> new NotFoundException("Корзина пользователя " + userName + " не найдена"));
    }

    private void checkUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new NotAuthorizedUserException("Имя пользователя не должно быть пустым!");
        }
    }

}
