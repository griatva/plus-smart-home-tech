package ru.yandex.practicum.collector.model.hubEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.collector.model.hubEvent.enums.ActionType;

@Data
public class DeviceAction {

    @NotBlank
    private String sensorId;//Идентификатор датчика, связанного с действием.

    @NotNull
    private ActionType type;//Перечисление возможных типов действий при срабатывании условия активации сценария.

    private Integer value;//Необязательное значение, связанное с действием.
}
