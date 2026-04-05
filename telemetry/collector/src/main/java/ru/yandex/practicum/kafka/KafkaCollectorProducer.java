package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.Properties;

@Slf4j
@Component
public class KafkaCollectorProducer implements AutoCloseable {
    private final KafkaProducer<String, SpecificRecordBase> kafkaProducer;
    private final Map<String, String> topics;
    private static final Duration PRODUCER_CLOSE_DURATION = Duration.ofSeconds(5);

    public KafkaCollectorProducer(KafkaProducerConfiguration config) {
        this.topics = validateTopics(config.getConfiguration().getTopics());
        this.kafkaProducer = createKafkaProducer(config.getConfiguration().getProperties());
    }

    private Map<String, String> validateTopics(Map<String, String> topics) {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalArgumentException("Kafka topics configuration is missing or empty");
        }
        return topics;
    }

    private KafkaProducer<String, SpecificRecordBase> createKafkaProducer(Map<String, String> configProps) {
        if (configProps == null || configProps.isEmpty()) {
            throw new IllegalArgumentException("Kafka producer configuration is missing");
        }

        if (!configProps.containsKey(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG)) {
            throw new IllegalArgumentException("bootstrap.servers must be configured");
        }

        Properties properties = new Properties();
        properties.putAll(configProps);

        return new KafkaProducer<>(properties);
    }

    public void send(KafkaTopic topic, String key, SpecificRecordBase value) {
        String topicName = topics.get(topic.getConfigKey());

        if (topicName == null) {
            throw new IllegalArgumentException("Topic not configured for key: " + topic);
        }

        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topicName, key, value);

        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.info("Error sending message to topic {}", topicName, exception);
            } else {
                log.debug("Message sent, partition {}, offset {}", metadata.partition(), metadata.offset());
            }
        });
    }

    @Override
    public void close() {
        log.info("Closing Kafka producer");
        kafkaProducer.flush();
        kafkaProducer.close(PRODUCER_CLOSE_DURATION);
    }
}
