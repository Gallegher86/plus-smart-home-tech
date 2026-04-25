package ru.yandex.practicum.handler.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

public class ScenarioRemovedEventHandler extends HubEventHandlerBase<ScenarioRemovedEventAvro> {

    public ScenarioRemovedEventHandler() {
        super(ScenarioRemovedEventAvro.class);
    }

    @Override
    protected void process(String hubId,
                           HubEventAvro event,
                           ScenarioRemovedEventAvro payload) {

        // здесь уже чистая бизнес-логика
    }
}
