package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;


public interface HubEventHandler {

    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}
