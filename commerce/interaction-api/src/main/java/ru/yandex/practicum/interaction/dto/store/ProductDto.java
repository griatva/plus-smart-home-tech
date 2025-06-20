package ru.yandex.practicum.interaction.dto.store;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.yandex.practicum.interaction.enums.ProductCategory;
import ru.yandex.practicum.interaction.enums.ProductState;
import ru.yandex.practicum.interaction.enums.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private UUID productId;

    @NotBlank
    private String productName;

    @NotBlank
    private String description;

    private String imageSrc; // Ссылка на картинку во внешнем хранилище или SVG

    @NotNull
    private QuantityState quantityState; // ENDED, FEW, ENOUGH, MANY

    @NotNull
    private ProductState productState; // ACTIVE, DEACTIVATE

    private ProductCategory productCategory; // LIGHTING, CONTROL, SENSORS

    @NotNull
    @DecimalMin(value = "1.0")
    private BigDecimal price;
}
