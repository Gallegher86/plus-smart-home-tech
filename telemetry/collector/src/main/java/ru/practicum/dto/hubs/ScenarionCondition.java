package ru.practicum.dto.hubs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScenarionCondition {
    @NotBlank
    private String sensorId;

    @NotNull
    private ScenarioType type;

    @NotNull
    private ScenarioOperation operation;

    private Integer value;
}
