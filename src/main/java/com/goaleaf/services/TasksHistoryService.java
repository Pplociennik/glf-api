package com.goaleaf.services;

import com.goaleaf.entities.TasksHistoryEntity;
import org.springframework.stereotype.Service;

@Service
public interface TasksHistoryService {

    Iterable<TasksHistoryEntity> getAllHabitHistory(Integer habitID);

    Iterable<TasksHistoryEntity> getUserHabitHistory(Integer habitID, Integer userID);

    Iterable<TasksHistoryEntity> getAllByTaskID(Integer taskID);
}
