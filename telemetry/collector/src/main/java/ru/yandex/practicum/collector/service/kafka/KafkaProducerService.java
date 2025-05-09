package ru.yandex.practicum.collector.service.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final Producer<String, SpecificRecordBase> producer;

    public void send(String topic, String key, SensorEventAvro message) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, key, message);
        producer.send(record);
    }

    public void send(String topic, String key, HubEventAvro message) {
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, key, message);
        producer.send(record);
    }
}
