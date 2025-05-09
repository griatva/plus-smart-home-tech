package ru.yandex.practicum.collector.service.handler.hub;

import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;


public interface HubEventHandler {

    String TOPIC = "telemetry.hubs.v1";

    HubEventType getMessageType();

    void handle(HubEvent event);
}
