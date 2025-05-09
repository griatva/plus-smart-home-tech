package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.SwitchSensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Component
@RequiredArgsConstructor
public class SwitchSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService service;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {

        SwitchSensorEvent event1 = (SwitchSensorEvent) event;

        SwitchSensorAvro switchSensorAvro = SwitchSensorAvro.newBuilder()
                .setState(event1.getState())
                .build();

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(event1.getId())
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(switchSensorAvro)
                .build();

        service.send(TOPIC, event1.getId(), message);
    }
}