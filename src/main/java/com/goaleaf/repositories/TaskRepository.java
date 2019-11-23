package com.goaleaf.repositories;

import com.goaleaf.entities.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, Integer> {

    Iterable<Task> findAll();

    Iterable<Task> getAllByHabitID(Integer habitID);

    Iterable<Task> getAllByCreatorID(Integer creatorID);

    Iterable<Task> getAllByCreatorIDAndHabitID(Integer creatorID, Integer habitID);

    Integer countAllByHabitID(Integer habitID);

    Integer countAllByCreatorID(Integer creatorID);

    Task getById(Integer taskID);

    Iterable<Task> findAllByIsCompleted(Boolean isCompleted);

    Iterable<Task> getAllByHabitIDAndExecutorID(Integer habitID, Integer executorID);
}
