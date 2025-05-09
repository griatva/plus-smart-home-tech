package ru.yandex.practicum.collector.model.sensorEvent;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {

    @NotNull
    private Integer linkQuality;
    @NotNull
    private Boolean motion;
    @NotNull
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
