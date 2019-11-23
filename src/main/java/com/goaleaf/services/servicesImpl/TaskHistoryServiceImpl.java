package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.TasksHistoryEntity;
import com.goaleaf.repositories.TaskHistoryRepository;
import com.goaleaf.services.TasksHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskHistoryServiceImpl implements TasksHistoryService {

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Override
    public Iterable<TasksHistoryEntity> getAllHabitHistory(Integer habitID) {
        return taskHistoryRepository.findAllByHabitID(habitID);
    }

    @Override
    public Iterable<TasksHistoryEntity> getUserHabitHistory(Integer habitID, Integer userID) {
        return taskHistoryRepository.findAllByHabitIDAndUserID(habitID, userID);
    }

    @Override
    public Iterable<TasksHistoryEntity> getAllByTaskID(Integer taskID) {
        return taskHistoryRepository.findAllByTaskID(taskID);
    }
}
