package ru.yandex.practicum.collector.service.handler.sensor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensorEvent.MotionSensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
public class MotionSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService service;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {

        MotionSensorEvent event1 = (MotionSensorEvent) event;

        MotionSensorAvro motionSensorAvro = MotionSensorAvro.newBuilder()
                .setLinkQuality(event1.getLinkQuality())
                .setMotion(event1.getMotion())
                .setVoltage(event1.getVoltage())
                .build();

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(event1.getId())
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(motionSensorAvro)
                .build();

        service.send(TOPIC, event1.getId(), message);
    }
}
