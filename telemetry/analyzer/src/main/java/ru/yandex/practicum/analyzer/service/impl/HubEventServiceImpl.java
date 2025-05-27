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
            log.warn("Неизвестный тип payload: {}", payload.getClass());
        }
    }

    @Override
    public void addDevice(String hubId, DeviceAddedEventAvro avro) {

        if (sensorRepository.existsByIdAndHubId(avro.getId(), hubId)) {
            throw new ConflictException("Такой датчик уже существует");
        }

        Sensor sensor = sensorMapper.toEntity(hubId, avro);
        sensorRepository.save(sensor);
        log.info("Сенсор добавлен: id={}, hubId={}, type={}", avro.getId(), hubId, avro.getType());
    }

    @Override
    public void removeDevice(DeviceRemovedEventAvro avro) {
        String sensorId = avro.getId();

        if (scenarioConditionRepository.existsBySensorId(sensorId) ||
                scenarioActionRepository.existsBySensorId(sensorId)) {
            throw new IllegalStateException("Нельзя удалить сенсор: он используется в сценариях.");
        }
        sensorRepository.deleteById(sensorId);
        log.info("Сенсор удалён: id={}", sensorId);
    }

    @Override
    @Transactional
    public void addScenario(String hubId, ScenarioAddedEventAvro avro) {

        // Собираем все уникальные sensorId
        Set<String> sensorIds = Stream.concat(
                avro.getConditions().stream().map(ScenarioConditionAvro::getSensorId),
                avro.getActions().stream().map(DeviceActionAvro::getSensorId)
        ).collect(Collectors.toSet());

        // Проверяем наличие всех сенсоров
        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);

        Set<String> found = sensors.stream().map(Sensor::getId).collect(Collectors.toSet());
        Set<String> missing = new HashSet<>(sensorIds);
        missing.removeAll(found);

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Сенсоры не найдены в хабе " + hubId + ": " + missing);
        }

        // Формируем Map для быстрого доступа
        Map<String, Sensor> sensorMap = sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, s -> s));

        // Создаём и сохраняем сценарий
        Scenario scenario = scenarioMapper.toEntity(hubId, avro);
        scenarioRepository.save(scenario);

        // Создаём и сохраняем условия
        List<ScenarioCondition> conditionLinks =
                scenarioMapper.toConditionLinks(scenario, avro.getConditions(), sensorMap);
        conditionRepository.saveAll(conditionLinks.stream().map(ScenarioCondition::getCondition).toList());
        scenarioConditionRepository.saveAll(conditionLinks);

        // Создаём и сохраняем действия
        List<ScenarioAction> actionLinks = scenarioMapper.toActionLinks(scenario, avro.getActions(), sensorMap);
        actionRepository.saveAll(actionLinks.stream().map(ScenarioAction::getAction).toList());
        scenarioActionRepository.saveAll(actionLinks);

        log.info("Сценарий '{}' успешно добавлен в хаб '{}'. Условий: {}, действий: {}",
                avro.getName(), hubId, conditionLinks.size(), actionLinks.size());
    }

    @Override
    public void removeScenario(String hubId, ScenarioRemovedEventAvro avro) {
        scenarioRepository.deleteByHubIdAndName(hubId, avro.getName());
        log.info("Сценарий '{}' удалён из хаба '{}'", avro.getName(), hubId);
    }
}
