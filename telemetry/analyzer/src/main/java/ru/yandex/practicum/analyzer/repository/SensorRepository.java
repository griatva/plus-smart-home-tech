package ru.yandex.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.analyzer.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, String> {

    boolean existsByIdAndHubId(String id, String hubId);

}
