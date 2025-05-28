package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


public interface HubEventHandler {

    String TOPIC = "telemetry.hubs.v1";

    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}
