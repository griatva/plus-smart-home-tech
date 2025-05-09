package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hubEvent.DeviceAddedEvent;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.hubEvent.enums.DeviceType;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
@RequiredArgsConstructor
public class DeviceAddedEventHandler implements HubEventHandler {

    private final KafkaProducerService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.DEVICE_ADDED;
    }


    @Override
    public void handle(HubEvent event) {
        DeviceAddedEvent event1 = (DeviceAddedEvent) event;

        DeviceAddedEventAvro deviceAddedEventAvro = DeviceAddedEventAvro.newBuilder()
                .setId(event1.getId())
                .setType(mapDeviceType(event1.getDeviceType()))
                .build();


        HubEventAvro message = HubEventAvro.newBuilder()
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(deviceAddedEventAvro)
                .build();

        service.send(TOPIC, event1.getHubId(), message);
    }

    private static DeviceTypeAvro mapDeviceType(DeviceType deviceType) {
        return DeviceTypeAvro.valueOf(deviceType.name());
    }
}
