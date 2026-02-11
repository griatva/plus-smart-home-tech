package ru.yandex.practicum.aggregator.consumer;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.aggregator.config.KafkaProperties;
import ru.yandex.practicum.aggregator.producer.KafkaSnapshotProducer;
import ru.yandex.practicum.aggregator.service.AggregationService;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final AggregationService aggregationService;
    private final KafkaSnapshotProducer producer;

    private final KafkaProperties kafkaProperties;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        String topic = kafkaProperties.getConsumer().getTopic();
        Duration pollTimeout = Duration.ofMillis(kafkaProperties.getConsumer().getPollTimeout());

        try {
            consumer.subscribe(List.of(topic));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(pollTimeout);
                if (!records.isEmpty()) {
                    log.info("Received {} messages from Kafka", records.count());
                }

                int count = 0;
                for (ConsumerRecord<String, SensorEventAvro> record : records) {
                    aggregationService.updateState(record.value())
                            .ifPresent(snapshot ->
                                    producer.send(
                                            kafkaProperties.getProducer().getTopic(),
                                            snapshot.getHubId(),
                                            snapshot
                                    )
                            );

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );

                    if (++count % 10 == 0) {
                        consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                            if (ex != null) {
                                log.warn("Error committing offsets: {}", offsets, ex);
                            }
                        });
                    }
                }

                consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                    if (ex != null) {
                        log.warn("Error during final offset commit: {}", offsets, ex);
                    }
                });
            }
        } catch (WakeupException ignored) {
            log.info("Shutdown signal received.");
        } catch (Exception e) {
            log.error("Error while processing sensor events", e);
        } finally {
            try {
                consumer.commitSync(currentOffsets);
                producer.flush();
            } finally {
                log.info("Closing Kafka consumer");
                consumer.close();
                log.info("Closing Kafka producer");
                producer.close();
            }
        }
    }
}