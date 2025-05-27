package ru.yandex.practicum.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Action;
import ru.yandex.practicum.analyzer.model.enums.ActionType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

@Component
public class ActionMapper {

    public Action toEntity(DeviceActionAvro avro) {
        ActionType type = ActionType.valueOf(avro.getType().name());
        Integer value = avro.getValue() != null ? (Integer) avro.getValue() : null;

        return new Action(null, type, value);
    }
}
