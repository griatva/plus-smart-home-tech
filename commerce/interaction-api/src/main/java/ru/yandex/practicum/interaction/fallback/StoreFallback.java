package ru.yandex.practicum.interaction.fallback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.interaction.client.StoreClient;
import ru.yandex.practicum.interaction.dto.store.PageableDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class StoreFallback implements StoreClient {

    @Override
    public Page<ProductDto> getProductsByCategoryPageable(ProductCategory category, PageableDto pageableDto) {
        System.out.println("Fallback: getProductsByCategoryPageable — сервис магазина недоступен, возвращаем пустую страницу.");
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        System.out.println("Fallback: createProduct — сервис магазина недоступен, товар не создан.");
        return emptyProductDto();
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        System.out.println("Fallback: updateProduct — сервис магазина недоступен, товар не обновлен.");
        return ProductDto.builder()
                .productId(productDto != null ? productDto.getProductId() : null)
                .build();
    }

    @Override
    public Boolean removeProduct(UUID productId) {
        System.out.println("Fallback: removeProduct — сервис магазина недоступен, товар не удален.");
        return false;
    }

    @Override
    public Boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        System.out.println("Fallback: updateQuantityState — сервис магазина недоступен, состояние количества не обновлено.");
        return false;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        System.out.println("Fallback: getProductById — сервис магазина недоступен, возвращаем пустой товар.");
        return emptyProductDto();
    }

    @Override
    public List<ProductDto> getProductListByIds(List<UUID> productIds) {
        System.out.println("Fallback: getProductListByIds — сервис магазина недоступен, возвращаем пустой список.");
        return Collections.emptyList();
    }

    private ProductDto emptyProductDto() {
        return ProductDto.builder().build();
    }
}