package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class SnapshotProcessor implements ApplicationRunner {
    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotService snapshotService;

    private final List<String> topics;
    private final Duration pollTimeout;
    private final int batchSize;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private int processedCount = 0;

    public SnapshotProcessor(
            KafkaConsumer<String, SensorsSnapshotAvro> consumer,
            AnalyzerKafkaProperties properties,
            SnapshotService snapshotService
    ) {
        this.consumer = consumer;
        this.snapshotService = snapshotService;

        this.topics = List.of(properties.getSnapshotConfiguration().getTopic());
        this.pollTimeout = properties.getSnapshotConfiguration().getPollTimeout();
        this.batchSize = properties.getSnapshotConfiguration().getBatchSize();
    }

    @Override
    public void run(ApplicationArguments args) {
        start();
    }

    private void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records =
                        consumer.poll(pollTimeout);

                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    snapshotService.handleSnapshot(record.value());
                    manageOffsets(record, consumer);
                }

                consumer.commitAsync(currentOffsets, (offsets, ex) -> {
                    if (ex != null) {
                        log.warn("commitAsync failed.", ex);
                    }
                });
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки снапшотов.", e);
        } finally {

            try {
                consumer.commitSync(currentOffsets);
            } finally {
                log.info("Закрываем консьюмер.");
                consumer.close();
            }
        }
    }

    private void manageOffsets(ConsumerRecord<String, SensorsSnapshotAvro> record,
                               KafkaConsumer<String, SensorsSnapshotAvro> consumer) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        processedCount++;

        if (processedCount % batchSize == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("Ошибка commitAsync, делаем commitSync.", exception);
                    consumer.commitSync(offsets);
                }
            });
        }
    }
}
