package ru.yandex.practicum.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.handler.hub.HubEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final AnalyzerKafkaProperties properties;
    private final Map<Class<?>, HubEventHandler> hubEventHandlers;
    private final Duration pollTimeout;

    public HubEventProcessor(
            KafkaConsumer<String, HubEventAvro> consumer,
            AnalyzerKafkaProperties properties,
            List<HubEventHandler> handlers
    ) {
        this.consumer = consumer;
        this.properties = properties;
        this.hubEventHandlers = handlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getPayloadType,
                        Function.identity()
                ));

        this.pollTimeout = properties.getHubEventConfiguration().getPollTimeout();
    }

    public void run() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            consumer.subscribe(List.of(properties.getHubEventConfiguration().getTopic()));

            while (true) {
                ConsumerRecords<String, HubEventAvro> records =
                        consumer.poll(pollTimeout);

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    Object payload = record.value().getPayload();
                    HubEventHandler handler = hubEventHandlers.get(payload.getClass());

                    if (handler == null) {
                        log.warn("Нет handler для payload: {}", payload.getClass());
                        continue;
                    }

                    handler.handle(record.key(), record.value());
                }

                consumer.commitAsync((offsets, ex) -> {
                    if (ex != null) {
                        log.warn("commit failed", ex);
                    }
                });
            }

        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки сообщений от хаба", e);
        } finally {

            try {
                consumer.commitSync();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close();
            }
        }
    }
}
