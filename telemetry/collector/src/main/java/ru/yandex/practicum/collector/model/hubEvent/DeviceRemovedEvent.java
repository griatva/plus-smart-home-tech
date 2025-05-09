package ru.yandex.practicum.collector.model.hubEvent;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class DeviceRemovedEvent extends HubEvent {

    @NotNull
    private String id; //удаляемое устройство

    @Override
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
