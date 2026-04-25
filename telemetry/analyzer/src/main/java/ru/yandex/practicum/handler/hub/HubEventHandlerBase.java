package ru.yandex.practicum.handler.hub;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Slf4j
public abstract class HubEventHandlerBase<T> implements HubEventHandler {
    private final Class<T> payloadType;

    protected HubEventHandlerBase(Class<T> payloadType) {
        this.payloadType = payloadType;
    }

    @Override
    public Class<?> getPayloadType() {
        return payloadType;
    }

    @Override
    public void handle(String hubId, HubEventAvro event) {
        Object payload = event.getPayload();

        if (!payloadType.isInstance(payload)) {
            throw new IllegalArgumentException(
                    "Неверный тип payload: " + payload.getClass()
            );
        }

        T typedPayload = payloadType.cast(payload);

        log.info("Обработка события: hubId={}, type={}",
                event.getHubId(),
                payloadType.getSimpleName());

        process(hubId, event, typedPayload);
    }

    protected abstract void process(String hubId, HubEventAvro event, T payload);
}
