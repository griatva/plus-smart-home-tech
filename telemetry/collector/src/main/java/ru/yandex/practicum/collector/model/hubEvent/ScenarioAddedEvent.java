package ru.yandex.practicum.collector.model.hubEvent;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioAddedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    private String name;// Название добавленного сценария.

    @Valid
    @NotEmpty
    private List<ScenarioCondition> conditions; // Список условий, которые связаны со сценарием.

    @Valid
    @NotEmpty
    private List<DeviceAction> actions; // Список действий, которые должны быть выполнены в рамках сценария.

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

}
