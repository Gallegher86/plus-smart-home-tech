package ru.yandex.practicum.handler.hub;

import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public class DeviceRemovedEventHandler extends HubEventHandlerBase<DeviceRemovedEventAvro> {

    public DeviceRemovedEventHandler() {
        super(DeviceRemovedEventAvro.class);
    }

    @Override
    protected void process(String hubId,
                           HubEventAvro event,
                           DeviceRemovedEventAvro payload) {

        // здесь уже чистая бизнес-логика
    }
}
