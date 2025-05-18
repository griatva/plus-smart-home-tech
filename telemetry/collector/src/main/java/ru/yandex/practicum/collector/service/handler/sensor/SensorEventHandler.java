package ru.yandex.practicum.collector.service.handler.sensor;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandler {

    String TOPIC = "telemetry.sensors.v1";

    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
