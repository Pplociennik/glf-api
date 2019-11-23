package com.goaleaf.controllers;

import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.User;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.TaskService;
import com.goaleaf.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Component
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private HabitService habitService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String generateModel() {
//        User user = new User(1, "DefaultUser", "def", "def", "def@default.com", "def");
//        userService.saveUser(user);
//
//        Habit habit = new Habit();
//        habit.setId(1);
//        habit.setCreatorLogin("def");
//        habit.setCreatorID(1);
//        habit.setCategory(Category.DIET);
//        habit.setHabitTitle("Potrawka Snafa");
//        habit.setPrivate(false);
//        habitService.saveHabit(habit);


//        TaskDTO taskDTO = new TaskDTO("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwiTG9naW4iOiJQc3plbWtvIiwiZXhwIjoxNTcyMTc2NTE1fQ.eByT27fDnGkAdMsUUswRTjF9NBqrvqDttd_W8h6-HrX6cFDu0mLgyWunMpG3GUQi0nbH8KWQRDI1hTTlyep4pQ"
//        , 1, "Bieda jedzonko z kolonii", 5);
//        taskService.saveTask(taskDTO);
        return "Generated default User!";
    }
}
