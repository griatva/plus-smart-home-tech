package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.model.Scenario;

import java.util.List;

public interface ScenarioRepository extends JpaRepository<Scenario, Long> {

    void deleteByHubIdAndName(String hubId, String name);

    List<Scenario> findAllByHubId(String hubId);
}
