package ru.yandex.practicum.collector.service.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties("collector.kafka")
public class KafkaProperties {

    private String bootstrapServers;
    private Producer producer;

    @Data
    public static class Producer {
        private String topicSensor;
        private String topicHub;
        private Map<String, String> properties;
    }
}