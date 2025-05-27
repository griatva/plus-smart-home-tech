package ru.yandex.practicum.analyzer.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.model.enums.DeviceType;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;

@Component
public class SensorMapper {

    public Sensor toEntity(String hubId, DeviceAddedEventAvro avro) {

        DeviceType type = DeviceType.valueOf(avro.getType().name());

        return new Sensor(avro.getId(), hubId, type);
    }
}