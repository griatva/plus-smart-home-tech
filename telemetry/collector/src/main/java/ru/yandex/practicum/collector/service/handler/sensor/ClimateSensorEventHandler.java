package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensorEvent.ClimateSensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
public class ClimateSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService service;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {
        ClimateSensorEvent event1 = (ClimateSensorEvent) event;

        ClimateSensorAvro climateSensorAvro = ClimateSensorAvro.newBuilder()
                .setCo2Level(event1.getCo2Level())
                .setHumidity(event1.getHumidity())
                .setTemperatureC(event1.getTemperatureC())
                .build();

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(event1.getId())
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(climateSensorAvro)
                .build();

        service.send(TOPIC, event1.getId(), message);
    }
}
