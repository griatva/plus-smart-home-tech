package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.model.Scenario;
import ru.yandex.practicum.analyzer.model.ScenarioAction;

import java.util.List;

public interface ScenarioActionRepository
        extends JpaRepository<ScenarioAction, ScenarioAction.ScenarioActionId> {

    boolean existsBySensorId(String sensorId);

    List<ScenarioAction> findAllByScenario(Scenario scenario);

}
