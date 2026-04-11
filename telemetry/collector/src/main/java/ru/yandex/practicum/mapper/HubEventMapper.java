package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface HubEventMapper {
    DeviceAddedEventAvro toDeviceAddedEventAvroFromProto(DeviceAddedEventProto event);

    DeviceRemovedEventAvro toDeviceRemovedEventAvroFromProto(DeviceRemovedEventProto event);

    @Mapping(source = "condition", target = "conditions")
    @Mapping(source = "action", target = "actions")
    ScenarioAddedEventAvro toScenarioAddedEventAvroFromProto(ScenarioAddedEventProto event);

    ScenarioRemovedEventAvro toScenarioRemovedEventAvroFromProto(ScenarioRemovedEventProto event);

    @Mapping(target = "value", expression = "java(mapValue(condition))")
    ScenarioConditionAvro toScenarioConditionAvroFromProto(ScenarioConditionProto condition);

    @Mapping(target = "value", expression = "java(mapDeviceActionValue(action))")
    DeviceActionAvro toDeviceActionAvroFromProto(DeviceActionProto action);

    ActionTypeAvro toActionTypeAvroFromProto(ActionTypeProto type);

    DeviceTypeAvro toDeviceTypeAvroFromProto(DeviceTypeProto type);

    ConditionTypeAvro toConditionTypeAvroFromProto(ConditionTypeProto type);

    ConditionOperationAvro toConditionOperationAvroFromProto(ConditionOperationProto operation);

    default Object mapValue(ScenarioConditionProto proto) {
        return switch (proto.getValueCase()) {
            case BOOL_VALUE -> proto.getBoolValue();
            case INT_VALUE -> proto.getIntValue();
            case VALUE_NOT_SET -> null;
            default -> throw new IllegalStateException("Неожиданное значение: " + proto.getValueCase());
        };
    }

    default Integer mapDeviceActionValue(DeviceActionProto proto) {
        return proto.hasValue() ? proto.getValue() : null;
    }
}
