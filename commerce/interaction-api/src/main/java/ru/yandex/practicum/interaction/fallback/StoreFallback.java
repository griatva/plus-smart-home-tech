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
        System.out.println("Fallback: getProductsByCategoryPageable — store service is unavailable, returning an empty page.");
        return new PageImpl<>(Collections.emptyList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        System.out.println("Fallback: createProduct — store service is unavailable, product was not created.");
        return emptyProductDto();
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        System.out.println("Fallback: updateProduct — store service is unavailable, product was not updated.");
        return ProductDto.builder()
                .productId(productDto != null ? productDto.getProductId() : null)
                .build();
    }

    @Override
    public Boolean removeProduct(UUID productId) {
        System.out.println("Fallback: removeProduct — store service is unavailable, product was not removed.");
        return false;
    }

    @Override
    public Boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        System.out.println("Fallback: updateQuantityState — store service is unavailable, quantity state was not updated.");
        return false;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        System.out.println("Fallback: getProductById — store service is unavailable, returning an empty product.");
        return emptyProductDto();
    }

    @Override
    public List<ProductDto> getProductListByIds(List<UUID> productIds) {
        System.out.println("Fallback: getProductListByIds — store service is unavailable, returning an empty list.");
        return Collections.emptyList();
    }

    private ProductDto emptyProductDto() {
        return ProductDto.builder().build();
    }
}
