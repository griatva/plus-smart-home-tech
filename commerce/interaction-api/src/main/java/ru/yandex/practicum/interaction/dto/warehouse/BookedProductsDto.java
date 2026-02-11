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
    private Double deliveryWeight; // total delivery weight

    @NotNull
    private Double deliveryVolume; // total delivery volume

    @NotNull
    private Boolean fragile; // indicates whether the delivery contains fragile items

}
