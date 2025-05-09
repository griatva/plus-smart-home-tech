package ru.yandex.practicum.collector.model.hubEvent;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioRemovedEvent extends HubEvent {

    @NotBlank
    @Size(min = 3)
    private String name;//Название удаленного сценария

    @Override
    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }

}
