package ru.yandex.practicum.interaction.dto.store;


import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageableDto {

    @Min(value = 0, message = "Minimum value for page is 0")
    private Integer page;

    @Min(value = 1, message = "Minimum value for size is 1")
    private Integer size;

    private List<String> sort;
}
