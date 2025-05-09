package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hubEvent.DeviceRemovedEvent;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceRemovedEventHandler implements HubEventHandler {

    private final KafkaProducerService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_REMOVED;
    }

    @Override
    public void handle(HubEvent event) {
        DeviceRemovedEvent event1 = (DeviceRemovedEvent) event;

        DeviceRemovedEventAvro deviceRemovedEventAvro = DeviceRemovedEventAvro.newBuilder()
                .setId(event1.getId())
                .build();


        HubEventAvro message = HubEventAvro.newBuilder()
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(deviceRemovedEventAvro)
                .build();

        service.send(TOPIC, event1.getHubId(), message);
    }

}
