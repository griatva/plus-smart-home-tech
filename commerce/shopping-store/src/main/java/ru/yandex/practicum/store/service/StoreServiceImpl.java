package ru.yandex.practicum.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.store.PageableDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.ProductState;
import ru.yandex.practicum.interaction.enums.QuantityState;
import ru.yandex.practicum.store.exception.ProductNotFoundException;
import ru.yandex.practicum.store.mapper.ProductMapper;
import ru.yandex.practicum.store.model.Product;
import ru.yandex.practicum.store.repository.StoreRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository repository;

    @Override
    public Page<ProductDto> getProductsByCategoryPageable(ProductCategory category, PageableDto pageableDto) {
        int page = pageableDto.getPage() != null ? pageableDto.getPage() : 0;
        int size = pageableDto.getSize() != null ? pageableDto.getSize() : 10;
        List<String> sortFields = pageableDto.getSort() != null ? pageableDto.getSort() : List.of();

        List<Sort.Order> orders = sortFields.stream()
                .map(field -> new Sort.Order(Sort.DEFAULT_DIRECTION, field))
                .toList();

        Pageable pageRequest = PageRequest.of(page, size, Sort.by(orders));

        Page<Product> productsPage = repository.findAllByProductCategory(category, pageRequest);
        return productsPage.map(ProductMapper::toDto);
    }

    @Transactional
    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product entity = ProductMapper.toEntity(productDto);
        Product saved = repository.save(entity);
        return ProductMapper.toDto(saved);
    }

    @Transactional
    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product oldProduct = getById(productDto.getProductId());

        oldProduct.setProductName(productDto.getProductName());
        oldProduct.setDescription(productDto.getDescription());
        oldProduct.setImageSrc(productDto.getImageSrc());
        oldProduct.setQuantityState(productDto.getQuantityState());
        oldProduct.setProductState(productDto.getProductState());
        oldProduct.setProductCategory(productDto.getProductCategory());
        oldProduct.setPrice(productDto.getPrice());

        Product saved = repository.save(oldProduct);
        return ProductMapper.toDto(saved);
    }

    @Transactional
    @Override
    public Boolean deactivateProduct(UUID productId) {
        Product product = getById(productId);
        product.setProductState(ProductState.DEACTIVATE);
        repository.save(product);
        return true;
    }

    @Transactional
    @Override
    public Boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        Product product = getById(productId);
        product.setQuantityState(quantityState);
        repository.save(product);
        return true;
    }

    @Override
    public ProductDto getProductById(UUID productId) {
        return ProductMapper.toDto(getById(productId));
    }

    private Product getById(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Товар с id " + productId + " не найден в БД!"));
    }
}
