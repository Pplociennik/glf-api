package com.goaleaf.controllers;

import com.goaleaf.entities.DTO.CompleteTaskDTO;
import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.DTO.pagination.TaskPageDTO;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.TasksHistoryEntity;
import com.goaleaf.entities.viewModels.NewTaskViewModel;
import com.goaleaf.security.SecurityConstants;
import com.goaleaf.services.TaskService;
import com.goaleaf.services.TasksHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/tasks")
@CrossOrigin(value = SecurityConstants.CLIENT_URL, maxAge = 3600)
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TasksHistoryService tasksHistoryService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<TaskDTO> getAll() {
        return taskService.getAllTasks();
    }

    @RequestMapping(value = "/habit", method = RequestMethod.GET)
    public Iterable<TaskDTO> getAllFromHabit(@RequestParam Integer habitID) {
        return taskService.getAllByHabitID(habitID);
    }

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Iterable<TaskDTO> getAllFromUser(@RequestParam Integer userID) {
        return taskService.getAllByCreatorID(userID);
    }

    @RequestMapping(value = "/user&habit", method = RequestMethod.GET)
    public Iterable<TaskDTO> getAllByUserFromHabit(@RequestParam Integer userID, Integer habiID) {
        return taskService.getAllByCreatorIDAndHabitID(userID, habiID);
    }

    @RequestMapping(value = "/task", method = RequestMethod.GET)
    public TaskDTO getOneByID(@RequestParam Integer taskID) {
        return taskService.getTaskByID(taskID);
    }

    @RequestMapping(value = "/count_usr", method = RequestMethod.GET)
    public Integer countTasksByUser(@RequestParam Integer userID) {
        return taskService.countUserTasks(userID);
    }

    @RequestMapping(value = "/count_hbt", method = RequestMethod.GET)
    public Integer countHabitTasks(@RequestParam Integer habitID) {
        return taskService.countHabitTasks(habitID);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public TaskDTO addTask(@RequestBody NewTaskViewModel newNewTaskViewModel) {
        return taskService.saveTask(newNewTaskViewModel);
    }

    @RequestMapping(value = "/complete", method = RequestMethod.POST)
    public Post completeTask(@RequestBody CompleteTaskDTO cmp) {
        return taskService.completeTask(cmp);
    }

    @RequestMapping(value = "/history", method = RequestMethod.GET)
    public Iterable<TasksHistoryEntity> getAllHabitHistory(@RequestParam Integer habitID) {
        return tasksHistoryService.getAllHabitHistory(habitID);
    }

    @RequestMapping(value = "/history/user", method = RequestMethod.GET)
    public Iterable<TasksHistoryEntity> getUserHabitHistory(@RequestParam Integer habitID, Integer userID) {
        return tasksHistoryService.getUserHabitHistory(habitID, userID);
    }

    @RequestMapping(value = "/history/task", method = RequestMethod.GET)
    public Iterable<TasksHistoryEntity> getAllTaskHistory(@RequestParam Integer taskID) {
        return tasksHistoryService.getAllByTaskID(taskID);
    }

    @RequestMapping(value = "/list/available", method = RequestMethod.GET)
    public Iterable<TaskDTO> getAvailableTasks(@RequestParam Integer habitID, Integer userID) {
        return taskService.getAvailableTasks(habitID, userID);
    }

    @RequestMapping(value = "/task/pushback", method = RequestMethod.DELETE)
    public HttpStatus removeTaskFromDatabase(@RequestParam Integer postID) {
        return taskService.pushBachTaskCompletion(postID);
    }

    @RequestMapping(value = "/task/remove", method = RequestMethod.DELETE)
    public HttpStatus justRemoveTask(@RequestParam Integer taskID) {
        return taskService.justRemoveTaskFromDatabase(taskID);
    }

    @GetMapping(value = "/available/paging")
    public TaskPageDTO getUserAvailableTasksPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Integer habitID, @RequestParam Integer userID) {
        return taskService.getAvailableTasksPaging(pageNr, objectsNr, habitID, userID);
    }

    @GetMapping(value = "/habit/paging")
    public TaskPageDTO getAllHabitTasksPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Integer habitID) {
        return taskService.getAllHabitTasksPaging(pageNr, objectsNr, habitID);
    }

}
