package ru.yandex.practicum.analyzer.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.analyzer.service.SnapshotService;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {

    private final KafkaConsumer<String, SensorsSnapshotAvro> snapshotKafkaConsumer;
    private final SnapshotService service;


    private static final String TOPIC = "telemetry.snapshots.v1";
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();


    public void start() {
        log.info("SnapshotProcessor запущен в потоке: {}", Thread.currentThread().getName());

        Runtime.getRuntime().addShutdownHook(new Thread(snapshotKafkaConsumer::wakeup));

        try {
            snapshotKafkaConsumer.subscribe(List.of(TOPIC));

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = snapshotKafkaConsumer.poll(POLL_TIMEOUT);
                if (!records.isEmpty()) {
                    log.info("Получено {} снапшотов от Kafka", records.count());
                }

                int count = 0;
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {

                    try {
                        log.info("Обработка снапшота: ключ={}, offset={}, partition={}",
                                record.key(), record.offset(), record.partition());
                        service.handleSnapshotRecord(record);
                    } catch (Exception e) {
                        log.error("Ошибка при обработке снапшота: {}", record, e);
                    }

                    currentOffsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );

                    if (++count % 10 == 0) {
                        commitAsyncSafe();
                    }
                }
                commitAsyncSafe();
                currentOffsets.clear();
            }
        } catch (WakeupException ignored) {
            log.info("Получен сигнал на завершение работы SnapshotProcessor.");
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов", e);
        } finally {
            try {
                snapshotKafkaConsumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем SnapshotConsumer");
                snapshotKafkaConsumer.close();
            }
        }
    }

    private void commitAsyncSafe() {
        snapshotKafkaConsumer.commitAsync(currentOffsets, (offsets, ex) -> {
            if (ex != null) {
                log.warn("Ошибка при коммите оффсетов SnapshotProcessor: {}", offsets, ex);
            }
        });
    }

}
