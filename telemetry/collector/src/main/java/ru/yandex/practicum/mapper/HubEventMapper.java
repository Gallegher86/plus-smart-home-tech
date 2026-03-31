package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.hubs.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubEventMapper {

    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toDeviceAddedEventAvro(DeviceAddedEvent event);

    DeviceRemovedEventAvro toDeviceRemovedEventAvro(DeviceRemovedEvent event);

    ScenarioAddedEventAvro toScenarioAddedEventAvro(ScenarioAddedEvent event);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvro(ScenarioRemovedEvent event);

    @Mapping(target = "sensor_id", source = "sensorId")
    ScenarioConditionAvro toScenarioConditionAvro(ScenarioCondition condition);

    @Mapping(target = "sensor_id", source = "sensorId")
    DeviceActionAvro toDeviceActionAvro(DeviceAction action);

    HubEventTypeAvro toHubEventTypeAvro(HubEventType type);
    ActionTypeAvro toActionTypeAvro(ActionType type);
    DeviceTypeAvro toDeviceType(DeviceType type);
    ConditionTypeAvro toConditionTypeAvro(ConditionType type);
    ConditionOperationAvro toConditionOperationAvro(ConditionOperation operation);
}
