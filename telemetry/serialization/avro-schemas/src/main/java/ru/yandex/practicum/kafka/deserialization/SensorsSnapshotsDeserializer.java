package ru.yandex.practicum.kafka.deserialization;

import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

public class SensorsSnapshotsDeserializer extends BaseAvroDeserializer<SensorsSnapshotAvro> {

    public SensorsSnapshotsDeserializer() {
        super(SensorsSnapshotAvro.getClassSchema());
    }
}
