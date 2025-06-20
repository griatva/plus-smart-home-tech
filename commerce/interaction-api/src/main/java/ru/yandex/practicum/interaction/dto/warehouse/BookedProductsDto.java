package ru.yandex.practicum.interaction.dto.warehouse;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookedProductsDto {

    @NotNull
    @Min(1)
    private Double deliveryWeight; // общий вес доставки

    @NotNull
    private Double deliveryVolume; // общий объем доставки

    @NotNull
    private Boolean fragile; // есть ли хрупкие вещи в доставке
}
