package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.model.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
    @Modifying
    @Query("DELETE FROM Action a " +
            "WHERE NOT EXISTS " +
            "(SELECT 1 FROM ScenarioAction sa " +
            "WHERE sa.action = a)")
    void deleteOrphans();
}
