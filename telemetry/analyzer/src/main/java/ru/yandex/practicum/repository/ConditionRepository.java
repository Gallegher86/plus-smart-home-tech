package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.Condition;

public interface ConditionRepository extends JpaRepository<Condition, Long> {
    @Modifying
    @Query("DELETE FROM Condition c " +
            "WHERE NOT EXISTS " +
            "(SELECT 1 FROM ScenarioCondition sc " +
            "WHERE sc.condition = c)")
    void deleteOrphans();
}
