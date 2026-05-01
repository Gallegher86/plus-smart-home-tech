package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.exception.DeviceNotFoundException;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.mapper.ActionMapper;
import ru.yandex.practicum.mapper.ConditionConverter;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScenarioServiceImpl implements ScenarioService {
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioActionRepository scenarioActionRepository;

    private final ConditionConverter conditionConverter;
    private final ActionMapper actionMapper;

    @Override
    @Transactional
    public Scenario save(String hubId, ScenarioAddedEventAvro event) {
        String name = event.getName();

        log.info("Запрос на сохранение/обновление сценария: hubId={}, name={}", hubId, name);

        Optional<Scenario> existing = scenarioRepository.findByHubIdAndName(hubId, name);

        if (existing.isPresent()) {
            return updateScenario(existing.get(), event);
        } else {
            return createScenario(hubId, event);
        }
    }

    @Override
    @Transactional
    public void delete(String hubId, String name) {
        log.info("Удаление сценария: hubId={}, name={}", hubId, name);

        scenarioRepository.findByHubIdAndName(hubId, name)
                .ifPresent(scenario -> {
                    Long scenarioId = scenario.getId();

                    scenarioConditionRepository.deleteByIdScenarioId(scenarioId);
                    scenarioActionRepository.deleteByIdScenarioId(scenarioId);

                    scenarioRepository.delete(scenario);

                    conditionRepository.deleteOrphans();
                    actionRepository.deleteOrphans();
                    log.info("Сценарий удален: id={}", scenario.getId());
                });
    }

    private Scenario createScenario(String hubId, ScenarioAddedEventAvro event) {
        Scenario scenario = new Scenario();
        scenario.setHubId(hubId);
        scenario.setName(event.getName());

        scenario = scenarioRepository.save(scenario);

        Map<String, Sensor> sensorMap = loadSensorsToMap(event);

        processConditions(event, sensorMap, scenario);
        processActions(event, sensorMap, scenario);

        log.info("Сценарий создан: id={}, hubId={}, name={}", scenario.getId(), hubId, scenario.getName());
        return scenario;
    }

    private Scenario updateScenario(Scenario scenario, ScenarioAddedEventAvro event) {
        scenarioConditionRepository.deleteByIdScenarioId(scenario.getId());
        scenarioActionRepository.deleteByIdScenarioId(scenario.getId());
        conditionRepository.deleteOrphans();
        actionRepository.deleteOrphans();

        Map<String, Sensor> sensorMap = loadSensorsToMap(event);

        processConditions(event, sensorMap, scenario);
        processActions(event, sensorMap, scenario);

        log.info("Сценарий обновлен: id={}, hubId={}, name={}", scenario.getId(),
                scenario.getHubId(), scenario.getName());
        return scenario;
    }

    private void processConditions(ScenarioAddedEventAvro event, Map<String, Sensor> sensorMap,
                                   Scenario scenario) {
        List<Condition> conditions = new ArrayList<>();
        List<ScenarioCondition> conditionLinks = new ArrayList<>();

        for (ScenarioConditionAvro c : event.getConditions()) {

            Sensor sensor = sensorMap.get(c.getSensorId());

            Condition condition = conditionConverter.fromAvro(c);
            conditions.add(condition);

            ScenarioCondition link = new ScenarioCondition();
            link.setScenario(scenario);
            link.setSensor(sensor);
            link.setCondition(condition);

            conditionLinks.add(link);
        }

        conditionRepository.saveAll(conditions);
        scenarioConditionRepository.saveAll(conditionLinks);
    }

    private void processActions(ScenarioAddedEventAvro event, Map<String, Sensor> sensorMap,
                                Scenario scenario) {
        List<Action> actions = new ArrayList<>();
        List<ScenarioAction> actionLinks = new ArrayList<>();

        for (DeviceActionAvro a : event.getActions()) {

            Sensor sensor = sensorMap.get(a.getSensorId());

            Action action = actionMapper.fromAvro(a);
            actions.add(action);

            ScenarioAction link = new ScenarioAction();
            link.setScenario(scenario);
            link.setSensor(sensor);
            link.setAction(action);

            actionLinks.add(link);
        }

        actionRepository.saveAll(actions);
        scenarioActionRepository.saveAll(actionLinks);
    }

    private Map<String, Sensor> loadSensorsToMap(ScenarioAddedEventAvro event) {
        Set<String> sensorIds = new HashSet<>();

        for (ScenarioConditionAvro c : event.getConditions()) {
            sensorIds.add(c.getSensorId());
        }

        for (DeviceActionAvro a : event.getActions()) {
            sensorIds.add(a.getSensorId());
        }

        List<Sensor> sensors = sensorRepository.findAllById(sensorIds);

        Map<String, Sensor> sensorMap = sensors.stream()
                .collect(Collectors.toMap(Sensor::getId, Function.identity()));

        validateAllSensorsFound(sensorMap, sensorIds);

        return sensorMap;
    }

    private void validateAllSensorsFound(Map<String, Sensor> sensorMap, Set<String> sensorIds) {
        if (sensorMap.size() != sensorIds.size()) {
            Set<String> found = sensorMap.keySet();
            Set<String> missing = new HashSet<>(sensorIds);
            missing.removeAll(found);

            log.warn("Не пройдена валидация сенсоров. найдены={}, отсутствуют={}", found, missing);

            throw new DeviceNotFoundException("Сенсоры не найдены: " + missing);
        }
    }
}
