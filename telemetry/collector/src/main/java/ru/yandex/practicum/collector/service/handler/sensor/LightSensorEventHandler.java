package ru.yandex.practicum.collector.service.handler.sensor;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.collector.service.kafka.KafkaProperties;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class LightSensorEventHandler implements SensorEventHandler {

    private final KafkaProducerService service;
    private final KafkaProperties kafkaProperties;

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    public void handle(SensorEventProto event) {
        LightSensorProto proto = event.getLightSensorEvent();

        LightSensorAvro lightSensorAvro = LightSensorAvro.newBuilder()
                .setLinkQuality(proto.getLinkQuality())
                .setLuminosity(proto.getLuminosity())
                .build();

        Instant instant = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(event.getId())
                .setHubId(event.getHubId())
                .setTimestamp(instant)
                .setPayload(lightSensorAvro)
                .build();

        String topic = kafkaProperties.getProducer().getTopicSensor();

        service.send(topic, event.getId(), message);
    }
}
