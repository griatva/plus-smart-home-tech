package ru.yandex.practicum.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Condition;
import ru.yandex.practicum.analyzer.model.enums.ConditionOperation;
import ru.yandex.practicum.analyzer.model.enums.ConditionType;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@Component
public class ConditionMapper {

    public Condition toEntity(ScenarioConditionAvro avro) {
        ConditionType type = ConditionType.valueOf(avro.getType().name());
        ConditionOperation operation = ConditionOperation.valueOf(avro.getOperation().name());

        Integer value = null;
        if (avro.getValue() instanceof Integer) {
            value = (Integer) avro.getValue();
        } else if (avro.getValue() instanceof Boolean) {
            value = (Boolean) avro.getValue() ? 1 : 0;
        }

        return new Condition(null, type, operation, value);
    }
}
