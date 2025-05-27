package ru.yandex.practicum.analyzer.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.*;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ScenarioMapper {

    private final ConditionMapper conditionMapper;
    private final ActionMapper actionMapper;

    public Scenario toEntity(String hubId, ScenarioAddedEventAvro avro) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(avro.getName());
        return scenario;
    }

    public List<ScenarioCondition> toConditionLinks(Scenario scenario,
                                                    List<ScenarioConditionAvro> avros,
                                                    Map<String, Sensor> sensorMap) {
        List<ScenarioCondition> result = new ArrayList<>();
        for (ScenarioConditionAvro avro : avros) {
            Sensor sensor = sensorMap.get(avro.getSensorId());
            Condition condition = conditionMapper.toEntity(avro);
            result.add(new ScenarioCondition(scenario, sensor, condition));
        }
        return result;
    }

    public List<ScenarioAction> toActionLinks(Scenario scenario,
                                              List<DeviceActionAvro> avros,
                                              Map<String, Sensor> sensorMap) {
        List<ScenarioAction> result = new ArrayList<>();
        for (DeviceActionAvro avro : avros) {
            Sensor sensor = sensorMap.get(avro.getSensorId());
            Action action = actionMapper.toEntity(avro);
            result.add(new ScenarioAction(scenario, sensor, action));
        }
        return result;
    }
}
