package ru.yandex.practicum.analyzer.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.analyzer.exception.ConflictException;
import ru.yandex.practicum.analyzer.mapper.ScenarioMapper;
import ru.yandex.practicum.analyzer.mapper.SensorMapper;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;
import ru.yandex.practicum.analyzer.model.Sensor;
import ru.yandex.practicum.analyzer.repository.*;
import ru.yandex.practicum.analyzer.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubEventServiceImpl implements HubEventService {

    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ActionRepository actionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    private final ScenarioMapper scenarioMapper;
    private final SensorMapper sensorMapper;


    public void handleHubEventRecord(ConsumerRecord<String, HubEventAvro> record) {
        String hubId = record.key();
        HubEventAvro event = record.value();
        Object payload = event.getPayload();

        if (payload instanceof DeviceAddedEventAvro deviceAdded) {
            addDevice(hubId, deviceAdded);
        } else if (payload instanceof DeviceRemovedEventAvro deviceRemoved) {
            removeDevice(deviceRemoved);
        } else if (payload instanceof ScenarioAddedEventAvro scenarioAdded) {
            addScenario(hubId, scenarioAdded);
        } else if (payload instanceof ScenarioRemovedEventAvro scenarioRemoved) {
            removeScenario(hubId, scenarioRemoved);
        } else {
            log.warn("Unknown payload type: {}", payload.getClass());
        }
    }

    @Override
    public void addDevice(String hubId, DeviceAddedEventAvro avro) {

        if (sensorRepository.existsByIdAndHubId(avro.getId(), hubId)) {
            throw new ConflictException("Sensor already exists");
        }

        Sensor sensor = sensorMapper.toEntity(hubId, avro);
        sensorRepository.save(sensor);
        log.info("Sensor added: id={}, hubId={}, type={}", avro.getId(), hubId, avro.getType());
    }

    @Override
    public void removeDevice(DeviceRemovedEventAvro avro) {
        String sensorId = avro.getId();

        if (scenarioConditionRepository.existsBySensorId(sensorId) ||
                scenarioActionRepository.existsBySensorId(sensorId)) {
            throw new IllegalStateException("Cannot delete sensor: it is used in scenarios.");
        }
        sensorRepository.deleteById(sensorId);
        log.info("Sensor deleted: id={}", sensorId);
    }

    @Override
    @Transactional
    public void addScenario(String hubId, ScenarioAddedEventAvro avro) {

        // Collect all unique sensorIds
        Set<String> sensorIds = Stream.concat(
                avro.getConditions().stream().map(ScenarioConditionAvro::getSensorId),
                avro.getActions().stream().map(DeviceActionAvro::getSensorId)
        ).collect(Collectors.toSet());

        // Verify that all sensors exist
        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);

        Set<String> found = sensors.stream().map(Sensor::getId).collect(Collectors.toSet());
        Set<String> missing = new HashSet<>(sensorIds);
        missing.removeAll(found);

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Sensors not found in hub " + hubId + ": " + missing);
        }

        // Build a Map for quick access
        Map<String, Sensor> sensorMap = sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, s -> s));

        // Create and save scenario
        Scenario scenario = scenarioMapper.toEntity(hubId, avro);
        scenarioRepository.save(scenario);

        // Create and save conditions
        List<ScenarioCondition> conditionLinks =
                scenarioMapper.toConditionLinks(scenario, avro.getConditions(), sensorMap);
        conditionRepository.saveAll(conditionLinks.stream().map(ScenarioCondition::getCondition).toList());
        scenarioConditionRepository.saveAll(conditionLinks);

        // Create and save actions
        List<ScenarioAction> actionLinks = scenarioMapper.toActionLinks(scenario, avro.getActions(), sensorMap);
        actionRepository.saveAll(actionLinks.stream().map(ScenarioAction::getAction).toList());
        scenarioActionRepository.saveAll(actionLinks);

        log.info("Scenario '{}' successfully added to hub '{}'. Conditions: {}, actions: {}",
                avro.getName(), hubId, conditionLinks.size(), actionLinks.size());
    }

    @Override
    public void removeScenario(String hubId, ScenarioRemovedEventAvro avro) {
        scenarioRepository.deleteByHubIdAndName(hubId, avro.getName());
        log.info("Scenario '{}' removed from hub '{}'", avro.getName(), hubId);
    }
}
