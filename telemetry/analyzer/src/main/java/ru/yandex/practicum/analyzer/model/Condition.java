package ru.yandex.practicum.analyzer.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.analyzer.model.enums.ConditionOperation;
import ru.yandex.practicum.analyzer.model.enums.ConditionType;

@Entity
@Table(name = "conditions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // sensor_id

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ConditionType type; // MOTION, LUMINOSITY, SWITCH, TEMPERATURE, CO2LEVEL, HUMIDITY

    @Enumerated(EnumType.STRING)
    @Column(name = "operation", nullable = false)
    private ConditionOperation operation; // EQUALS, GREATER_THAN, LOWER_THAN

    @Column(name = "value")
    private Integer value;
}
