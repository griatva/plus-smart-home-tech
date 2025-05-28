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
import ru.yandex.practicum.analyzer.service.HubEventService;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {

    private final KafkaConsumer<String, HubEventAvro> hubEventKafkaConsumer;
    private final HubEventService service;

    private static final String TOPIC = "telemetry.hubs.v1";
    private static final Duration POLL_TIMEOUT = Duration.ofMillis(1000);

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();


    @Override
    public void run() {
        log.info("HubEventProcessor запущен в потоке: {}", Thread.currentThread().getName());

        Runtime.getRuntime().addShutdownHook(new Thread(hubEventKafkaConsumer::wakeup));


        try {
            hubEventKafkaConsumer.subscribe(List.of(TOPIC));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records = hubEventKafkaConsumer.poll(POLL_TIMEOUT);
                if (!records.isEmpty()) {
                    log.info("Получено {} сообщений от Kafka", records.count());
                }

                int count = 0;
                for (ConsumerRecord<String, HubEventAvro> record : records) {

                    try {
                        log.info("Обработка HubEvent: ключ={}, offset={}, partition={}",
                                record.key(), record.offset(), record.partition());
                        service.handleHubEventRecord(record);
                    } catch (Exception e) {
                        log.error("Ошибка при обработке события от хаба: {}", record, e);
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
            log.info("Получен сигнал на завершение работы HubEventProcessor.");
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от хаба", e);
        } finally {
            try {
                hubEventKafkaConsumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем HubEvenConsumer");
                hubEventKafkaConsumer.close();
            }
        }
    }

    private void commitAsyncSafe() {
        hubEventKafkaConsumer.commitAsync(currentOffsets, (offsets, ex) -> {
            if (ex != null) {
                log.warn("Ошибка при коммите оффсетов HubEventProcessor: {}", offsets, ex);
            }
        });
    }
}
