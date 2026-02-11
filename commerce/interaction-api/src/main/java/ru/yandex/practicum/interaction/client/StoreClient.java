package ru.yandex.practicum.interaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.interaction.config.FeignConfig;
import ru.yandex.practicum.interaction.dto.store.PageableDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.QuantityState;
import ru.yandex.practicum.interaction.fallback.StoreFallback;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "shopping-store",
        path = "/api/v1/shopping-store",
        configuration = FeignConfig.class,
        fallback = StoreFallback.class)
public interface StoreClient {

    @GetMapping
    Page<ProductDto> getProductsByCategoryPageable(@RequestParam ProductCategory category, PageableDto pageableDto);

    @PutMapping
    ProductDto createProduct(@RequestBody ProductDto productDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody ProductDto productDto);

    @PostMapping("/removeProductFromStore")
    Boolean removeProduct(@RequestBody UUID productId);

    @PostMapping("/quantityState")
    Boolean updateQuantityState(@RequestParam UUID productId,
                                @RequestParam QuantityState quantityState);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable UUID productId);

    @PostMapping("/list")
    List<ProductDto> getProductListByIds(@RequestBody List<UUID> productIds);

}