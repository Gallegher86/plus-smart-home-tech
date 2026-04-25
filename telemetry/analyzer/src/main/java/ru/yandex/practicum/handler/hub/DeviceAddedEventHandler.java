package ru.yandex.practicum.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedEventHandler extends HubEventHandlerBase<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler() {
        super(DeviceAddedEventAvro.class);
    }

    @Override
    protected void process(String hubId,
                           HubEventAvro event,
                           DeviceAddedEventAvro payload) {

        // здесь уже чистая бизнес-логика
    }
}
