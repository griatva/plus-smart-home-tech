package ru.yandex.practicum.collector.service.handler.sensor;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.TemperatureSensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Component
@RequiredArgsConstructor
public class TemperatureSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService service;

    @Override
    public SensorEventType getMessageType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEvent event) {

        TemperatureSensorEvent event1 = (TemperatureSensorEvent) event;

        TemperatureSensorAvro temperatureSensorAvro = TemperatureSensorAvro.newBuilder()
                .setId(event1.getId())
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setTemperatureC(event1.getTemperatureC())
                .setTemperatureF(event1.getTemperatureF())
                .build();

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(event1.getId())
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(temperatureSensorAvro)
                .build();

        service.send(TOPIC, event1.getId(), message);
    }
}