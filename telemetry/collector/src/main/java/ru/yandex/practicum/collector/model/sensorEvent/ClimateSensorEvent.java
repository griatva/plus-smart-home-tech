package ru.yandex.practicum.collector.model.sensorEvent;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {

    @NotNull
    private Integer temperatureC;
    @NotNull
    private Integer humidity;
    @NotNull
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}
