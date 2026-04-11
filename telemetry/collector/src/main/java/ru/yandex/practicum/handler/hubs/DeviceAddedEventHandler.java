package ru.yandex.practicum.handler.hubs;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.KafkaCollectorProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.mapper.HubEventMapper;

public class DeviceAddedEventHandler extends HubEventHandlerBase<DeviceAddedEventAvro> {

    public DeviceAddedEventHandler(KafkaCollectorProducer producer, HubEventMapper mapper) {
        super(producer, mapper);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.DEVICE_ADDED;
    }

    @Override
    protected DeviceAddedEventAvro mapToHubEventAvroPayload(HubEventProto event) {
        return mapper.toDeviceAddedEventAvroFromProto(event.getDeviceAdded());
    }
}
