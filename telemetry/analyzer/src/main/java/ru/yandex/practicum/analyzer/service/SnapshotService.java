package ru.yandex.practicum.analyzer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public interface SnapshotService {
    void handleSnapshotRecord(ConsumerRecord<String, SensorsSnapshotAvro> record);

}
