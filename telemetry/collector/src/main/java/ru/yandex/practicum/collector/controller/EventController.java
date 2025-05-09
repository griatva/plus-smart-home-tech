package ru.yandex.practicum.collector.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.collector.model.hubEvent.HubEvent;
import ru.yandex.practicum.collector.model.hubEvent.enums.HubEventType;
import ru.yandex.practicum.collector.model.sensorEvent.SensorEvent;
import ru.yandex.practicum.collector.model.sensorEvent.enums.SensorEventType;
import ru.yandex.practicum.collector.service.handler.hub.HubEventHandler;
import ru.yandex.practicum.collector.service.handler.sensor.SensorEventHandler;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
public class EventController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(List<SensorEventHandler> sensorEventHandlers, List<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getMessageType, Function.identity()));
        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getMessageType, Function.identity()));
    }


    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent request) {
        if (sensorEventHandlers.containsKey(request.getType())) {
            sensorEventHandlers.get(request.getType()).handle(request);
        } else {
            throw new IllegalArgumentException("Не найден обработчик события " + request);
        }
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
        if (hubEventHandlers.containsKey(request.getType())) {
            hubEventHandlers.get(request.getType()).handle(request);
        } else {
            throw new IllegalArgumentException("Не найден обработчик события " + request);
        }
    }

}
