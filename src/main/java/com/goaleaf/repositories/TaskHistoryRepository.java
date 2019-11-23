package com.goaleaf.repositories;

import com.goaleaf.entities.TasksHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TaskHistoryRepository extends CrudRepository<TasksHistoryEntity, Integer>, JpaRepository<TasksHistoryEntity, Integer> {

    Iterable<TasksHistoryEntity> findAllByHabitID(Integer habitID);

    Iterable<TasksHistoryEntity> findAllByHabitIDAndUserID(Integer habitID, Integer userID);

    Iterable<TasksHistoryEntity> findAllByTaskID(Integer taskID);

    Iterable<TasksHistoryEntity> findAllByTaskIDAndUserID(Integer taskID, Integer userID);
}
