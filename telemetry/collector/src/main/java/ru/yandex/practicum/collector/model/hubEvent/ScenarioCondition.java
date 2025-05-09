package ru.yandex.practicum.collector.model.hubEvent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.collector.model.hubEvent.enums.ConditionOperation;
import ru.yandex.practicum.collector.model.hubEvent.enums.ConditionType;

@Data
public class ScenarioCondition {

    @NotBlank
    private String sensorId; // Идентификатор датчика, связанного с условием.

    @NotNull
    private ConditionType type; // Типы условий, которые могут использоваться в сценариях.

    @NotNull
    private ConditionOperation operation; // Операции, которые могут быть использованы в условиях.

    private Integer value; //Значение, используемое в условии
}
