package ru.yandex.practicum.collector.service.handler.hub;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.hubEvent.ScenarioRemovedEvent;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Component
@RequiredArgsConstructor
public class ScenarioRemovedEventHandler implements HubEventHandler {

    private final KafkaProducerService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    @Override
    public void handle(HubEvent event) {
        ScenarioRemovedEvent event1 = (ScenarioRemovedEvent) event;

        ScenarioRemovedEventAvro scenarioRemovedEventAvro = ScenarioRemovedEventAvro.newBuilder()
                .setName(event1.getName())
                .build();


        HubEventAvro message = HubEventAvro.newBuilder()
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(scenarioRemovedEventAvro)
                .build();

        service.send(TOPIC, event1.getHubId(), message);
    }
}
