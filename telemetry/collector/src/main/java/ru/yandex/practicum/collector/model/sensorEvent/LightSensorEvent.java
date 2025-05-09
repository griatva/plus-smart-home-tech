package ru.yandex.practicum.collector.model.sensorEvent;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;


@Getter
@Setter
@ToString(callSuper = true)
public class LightSensorEvent extends SensorEvent {

    @NotNull
    private Integer linkQuality;
    @NotNull
    private Integer luminosity;

    @Override
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}