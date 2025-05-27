package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioCondition;

import java.util.List;

public interface ScenarioConditionRepository
        extends JpaRepository<ScenarioCondition, ScenarioCondition.ScenarioConditionId> {

    boolean existsBySensorId(String sensorId);

    List<ScenarioCondition> findAllByScenarioIn(List<Scenario> scenarios);
}
