package com.goaleaf.services;

import com.goaleaf.entities.DTO.CompleteTaskDTO;
import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.DTO.pagination.TaskPageDTO;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.viewModels.NewTaskViewModel;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public interface TaskService {

    Iterable<TaskDTO> getAllTasks();

    Iterable<TaskDTO> getAllByCreatorID(Integer creatorID);

    Iterable<TaskDTO> getAllByHabitID(Integer habitID);

    Iterable<TaskDTO> getAllByCreatorIDAndHabitID(Integer creatorID, Integer habitID);

    Integer countUserTasks(Integer userID);

    Integer countHabitTasks(Integer habitID);

    TaskDTO saveTask(NewTaskViewModel newTask);

    TaskDTO getTaskByID(Integer taskID);

    Post completeTask(CompleteTaskDTO cmp);

    Iterable<TaskDTO> getAvailableTasks(Integer habitID, Integer userID);

    HttpStatus pushBachTaskCompletion(Integer taskID);

    HttpStatus justRemoveTaskFromDatabase(Integer taskID);

    TaskPageDTO getAvailableTasksPaging(Integer pageNr, Integer objectsNr, Integer habitID, Integer userID);

    TaskPageDTO getAllHabitTasksPaging(Integer pageNr, Integer objectsNr, Integer habitID);

}
