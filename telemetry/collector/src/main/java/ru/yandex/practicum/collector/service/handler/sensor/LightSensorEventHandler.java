package ru.yandex.practicum.collector.service.handler.sensor;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensorEvent.LightSensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService service;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {
        LightSensorEvent event1 = (LightSensorEvent) event;

        LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(event1.getLinkQuality())
                .setLuminosity(event1.getLuminosity())
                .build();

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(event1.getId())
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(lightSensorAvro)
                .build();

        service.send(TOPIC, event1.getId(), message);
    }
}
