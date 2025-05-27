package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "scenario_conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(ScenarioCondition.ScenarioConditionId.class)
public class ScenarioCondition {

    @Id
    @ManyToOne
    @JoinColumn(name = "scenario_id")
    private Scenario scenario;

    @Id
    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Id
    @ManyToOne
    @JoinColumn(name = "condition_id")
    private Condition condition;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScenarioConditionId implements Serializable {
        private Long scenario;
        private String sensor;
        private Long condition;
    }
}


