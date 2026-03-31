package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.sensors.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Mapper(componentModel = "spring")
public interface SensorEventMapper {
    @Mapping(source = "temperatureC", target = "temperature_c")
    @Mapping(source = "co2Level", target = "co2_level")
    ClimateSensorAvro toClimateSensorEventAvro(ClimateSensorEvent event);

    @Mapping(source = "linkQuality", target = "link_quality")
    LightSensorAvro toLightSensorEventAvro(LightSensorEvent event);

    @Mapping(source = "linkQuality", target = "link_quality")
    MotionSensorAvro toMotionSensorEventAvro(MotionSensorEvent event);

    SwitchSensorAvro toSwitchSensorEventAvro(SwitchSensorEvent event);

    @Mapping(source = "temperatureC", target = "temperature_c")
    @Mapping(source = "temperatureF", target = "temperature_f")
    TemperatureSensorAvro toTemperatureSensorEventAvro(TemperatureSensorEvent event);

    SensorEventTypeAvro toSensorEventTypeAvro(SensorEventType type);
}
