package ru.yandex.practicum.analyzer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.model.enums.ConditionOperation;
import ru.yandex.practicum.analyzer.model.enums.ConditionType;
import ru.yandex.practicum.analyzer.repository.ScenarioActionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioConditionRepository;
import ru.yandex.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.analyzer.repository.SensorRepository;
import ru.yandex.practicum.analyzer.service.SnapshotService;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SnapshotServiceImpl implements SnapshotService {

    @GrpcClient("hub-router")
    private HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouterClient;

    private final ScenarioRepository scenarioRepository;
    private final ScenarioConditionRepository conditionRepository;
    private final ScenarioActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Override
    @Transactional
    public void handleSnapshotRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        String hubId = record.key();
        SensorsSnapshotAvro snapshotAvro = record.value();
        Map<String, SensorStateAvro> sensorStates = snapshotAvro.getSensorsState(); // key = sensorId

        // Verify that all sensors from the snapshot exist in the database
        List<String> sensorIds = new ArrayList<>(sensorStates.keySet());
        List<Sensor> knownSensors = sensorRepository.findAllById(sensorIds);
        Map<String, Sensor> sensorMap = knownSensors.stream()
                .collect(Collectors.toMap(Sensor::getId, s -> s));

        for (String sensorId : sensorIds) {
            if (!sensorMap.containsKey(sensorId)) {
                log.warn("Sensor {} was not found in the database. No scenarios will be triggered for this sensor.",
                        sensorId);
            }
        }

        // Retrieve all scenarios for the hub
        List<Scenario> scenarios = scenarioRepository.findAllByHubId(hubId);

        // Retrieve all conditions
        List<ScenarioCondition> allConditions = conditionRepository.findAllByScenarioIn(scenarios);

        Map<Scenario, List<ScenarioCondition>> conditionsByScenario = allConditions.stream()
                .collect(Collectors.groupingBy(ScenarioCondition::getScenario));

        // Evaluate each scenario
        for (Scenario scenario : scenarios) {
            List<ScenarioCondition> conditions = conditionsByScenario.getOrDefault(scenario, List.of());

            boolean allMatch = conditions.stream().allMatch(condition -> {
                String sensorId = condition.getSensor().getId();
                SensorStateAvro stateAvro = sensorStates.get(sensorId);

                if (stateAvro == null) {
                    log.debug("No data received from sensor {} for scenario {}", sensorId, scenario.getName());
                    return false;
                }

                Object actualValue = extractSensorValue(stateAvro, condition.getCondition().getType());
                Integer expectedValue = condition.getCondition().getValue();
                ConditionOperation operation = condition.getCondition().getOperation();

                return compare(actualValue, expectedValue, operation);
            });

            if (allMatch) {
                log.info("Scenario '{}' conditions are met. Sending actions.", scenario.getName());

                List<ScenarioAction> actions = actionRepository.findAllByScenario(scenario);
                for (ScenarioAction action : actions) {
                    DeviceActionRequest request = DeviceActionRequest.newBuilder()
                            .setHubId(hubId)
                            .setScenarioName(scenario.getName())
                            .setTimestamp(com.google.protobuf.Timestamp.newBuilder()
                                    .setSeconds(snapshotAvro.getTimestamp().getEpochSecond())
                                    .setNanos(snapshotAvro.getTimestamp().getNano())
                                    .build())
                            .setAction(DeviceActionProto.newBuilder()
                                    .setSensorId(action.getSensor().getId())
                                    .setTypeValue(action.getAction().getType().ordinal())
                                    .setValue(action.getAction().getValue() != null ? action.getAction().getValue() : 0)
                                    .build())
                            .build();

                    hubRouterClient.handleDeviceAction(request);
                    log.info("Action sent for sensor {}: {}", action.getSensor().getId(), action.getAction());
                }
            }
        }
    }

    private Object extractSensorValue(SensorStateAvro stateAvro, ConditionType type) {
        Object payload = stateAvro.getData();

        if (payload instanceof ClimateSensorAvro climate) {
            return switch (type) {
                case TEMPERATURE -> climate.getTemperatureC();
                case HUMIDITY -> climate.getHumidity();
                case CO2LEVEL -> climate.getCo2Level();
                default -> null;
            };
        } else if (payload instanceof TemperatureSensorAvro temperature) {
            return switch (type) {
                case TEMPERATURE -> temperature.getTemperatureC();
                default -> null;
            };
        } else if (payload instanceof LightSensorAvro light) {
            return switch (type) {
                case LUMINOSITY -> light.getLuminosity();
                default -> null;
            };
        } else if (payload instanceof MotionSensorAvro motion) {
            return switch (type) {
                case MOTION -> motion.getMotion() ? 1 : 0;
                default -> null;
            };
        } else if (payload instanceof SwitchSensorAvro sw) {
            return switch (type) {
                case SWITCH -> sw.getState() ? 1 : 0;
                default -> null;
            };
        }

        log.warn("Unable to map condition type {} to payload {}", type, payload.getClass().getSimpleName());
        return null;
    }

    private boolean compare(Object actual, Integer expected, ConditionOperation op) {
        if (!(actual instanceof Integer actualInt)) return false;
        return switch (op) {
            case EQUALS -> actualInt.equals(expected);
            case GREATER_THAN -> actualInt > expected;
            case LOWER_THAN -> actualInt < expected;
        };
    }
}