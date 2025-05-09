package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.model.hubEvent.DeviceAction;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.hubEvent.ScenarioAddedEvent;
import ru.yandex.practicum.collector.model.hubEvent.ScenarioCondition;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final KafkaProducerService service;

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEvent event) {
        ScenarioAddedEvent event1 = (ScenarioAddedEvent) event;

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(event1.getName())
                .setConditions(getAvroConditions(event1.getConditions()))
                .setActions(getAvroActions(event1.getActions()))
                .build();


        HubEventAvro message = HubEventAvro.newBuilder()
                .setHubId(event1.getHubId())
                .setTimestamp(event1.getTimestamp())
                .setPayload(scenarioAddedEventAvro)
                .build();

        service.send(TOPIC, event1.getHubId(), message);
    }


    private List<ScenarioConditionAvro> getAvroConditions(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(condition -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(condition.getSensorId())
                        .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                        .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()))
                        .setValue(condition.getValue())
                        .build())
                .toList();
    }

    private List<DeviceActionAvro> getAvroActions(List<DeviceAction> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }


}
