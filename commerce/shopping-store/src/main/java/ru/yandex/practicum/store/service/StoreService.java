package ru.yandex.practicum.store.service;

import org.springframework.data.domain.Page;
import ru.yandex.practicum.interaction.dto.store.PageableDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.util.UUID;

public interface StoreService {

    Page<ProductDto> getProductsByCategoryPageable(ProductCategory category, PageableDto pageableDto);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(ProductDto productDto);

    Boolean deactivateProduct(UUID productId);

    Boolean updateQuantityState(UUID productId, QuantityState quantityState);

    ProductDto getProductById(UUID productId);
}
