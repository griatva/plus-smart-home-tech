package ru.yandex.practicum.analyzer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.yandex.practicum.kafka.telemetry.event.*;

public interface HubEventService {

    void handleHubEventRecord(ConsumerRecord<String, HubEventAvro> record);

    void addDevice(String hubId, DeviceAddedEventAvro avro);

    void removeDevice(DeviceRemovedEventAvro avro);

    void addScenario(String hubId, ScenarioAddedEventAvro avro);

    void removeScenario(String hubId, ScenarioRemovedEventAvro avro);


}
