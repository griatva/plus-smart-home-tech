package ru.yandex.practicum.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.collector.service.kafka.KafkaProducerService;
import ru.yandex.practicum.collector.service.kafka.KafkaProperties;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedEventHandler implements HubEventHandler {

    private final KafkaProducerService service;
    private final KafkaProperties kafkaProperties;

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    public void handle(HubEventProto event) {
        ScenarioAddedEventProto proto = event.getScenarioAdded();

        ScenarioAddedEventAvro scenarioAddedEventAvro = ScenarioAddedEventAvro.newBuilder()
                .setName(proto.getName())
                .setConditions(getAvroConditions(proto.getConditionList()))
                .setActions(getAvroActions(proto.getActionList()))
                .build();

        Instant instant = Instant.ofEpochSecond(
                event.getTimestamp().getSeconds(),
                event.getTimestamp().getNanos()
        );


        HubEventAvro message = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(instant)
                .setPayload(scenarioAddedEventAvro)
                .build();


        String topic = kafkaProperties.getProducer().getTopicHub();
        service.send(topic, event.getHubId(), message);
    }


    private List<ScenarioConditionAvro> getAvroConditions(List<ScenarioConditionProto> conditions) {


        return conditions.stream()
                .map(condition -> {
                    ScenarioConditionAvro.Builder builder = ScenarioConditionAvro.newBuilder()
                            .setSensorId(condition.getSensorId())
                            .setType(ConditionTypeAvro.valueOf(condition.getType().name()))
                            .setOperation(ConditionOperationAvro.valueOf(condition.getOperation().name()));

                    switch (condition.getValueCase()) {
                        case BOOL_VALUE -> builder.setValue(condition.getBoolValue());
                        case INT_VALUE -> builder.setValue(condition.getIntValue());
                        case VALUE_NOT_SET -> builder.setValue(null);
                    }

                    return builder.build();
                })
                .toList();
    }

    private List<DeviceActionAvro> getAvroActions(List<DeviceActionProto> actions) {
        return actions.stream()
                .map(action -> DeviceActionAvro.newBuilder()
                        .setSensorId(action.getSensorId())
                        .setType(ActionTypeAvro.valueOf(action.getType().name()))
                        .setValue(action.getValue())
                        .build())
                .toList();
    }


}
