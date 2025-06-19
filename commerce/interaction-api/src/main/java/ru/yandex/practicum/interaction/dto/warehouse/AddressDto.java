package ru.yandex.practicum.interaction.dto.warehouse;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    String country;
    String city;
    String street;
    String house;
    String flat;
}
