package ru.yandex.practicum.store.mapper;

import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.store.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
        if (product == null) return null;

        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .description(product.getDescription())
                .imageSrc(product.getImageSrc())
                .quantityState(product.getQuantityState())
                .productState(product.getProductState())
                .productCategory(product.getProductCategory())
                .price(product.getPrice())
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) return null;

        return Product.builder()
                .productId(dto.getProductId())
                .productName(dto.getProductName())
                .description(dto.getDescription())
                .imageSrc(dto.getImageSrc())
                .quantityState(dto.getQuantityState())
                .productState(dto.getProductState())
                .productCategory(dto.getProductCategory())
                .price(dto.getPrice())
                .build();
    }

    public static List<ProductDto> toDtoList(List<Product> products) {
        if (products == null) return null;

        return products.stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<Product> toEntityList(List<ProductDto> dtos) {
        if (dtos == null) return null;

        return dtos.stream()
                .map(ProductMapper::toEntity)
                .collect(Collectors.toList());
    }
}