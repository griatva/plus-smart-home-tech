package ru.yandex.practicum.collector.service.handler.sensor;

import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;

public interface SensorEventHandler {

    String TOPIC = "telemetry.sensors.v1";

    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
