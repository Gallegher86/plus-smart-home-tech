package ru.yandex.practicum.handler.hub;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

public class ScenarioAddedEventHandler extends HubEventHandlerBase<ScenarioAddedEventAvro> {

    public ScenarioAddedEventHandler() {
        super(ScenarioAddedEventAvro.class);
    }

    @Override
    protected void process(String hubId,
                           HubEventAvro event,
                           ScenarioAddedEventAvro payload) {

        // здесь уже чистая бизнес-логика
    }
}
