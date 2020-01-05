package com.goaleaf.engine;

import com.goaleaf.controllers.AuthController;
import com.goaleaf.controllers.HabitController;
import com.goaleaf.entities.DTO.CompleteTaskDTO;
import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.User;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;
import com.goaleaf.entities.viewModels.NewTaskViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.LoginViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.repositories.HabitRepository;
import com.goaleaf.repositories.UserRepository;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.TaskService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TaskServiceTests {

    @Autowired
    private AuthController authController;
    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private HabitController habitController;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HabitRepository habitRepository;

    private Set<User> toClean;

    @Before
    public void prepare() {
        this.toClean = new HashSet<>(0);
    }

    @After
    public void clean() {
        this.toClean.forEach(user -> userRepository.delete(user));
    }

    @Test
    public void whenTaskCompleted_thenOK() throws BadCredentialsException, MessagingException, AccountNotExistsException {

        RegisterViewModel model = new RegisterViewModel();
        model.login = "tester";
        model.userName = "tester";
        model.emailAddress = "email@email.com";
        model.password = "password";
        model.matchingPassword = "password";

        UserDTO dto = userService.registerNewUserAccount(model);

        this.toClean.add(userRepository.findByLogin(model.login));

        LoginViewModel loginViewModel = new LoginViewModel();
        loginViewModel.login = "tester";
        loginViewModel.password = "password";

        String token = authController.login(loginViewModel);

        HabitViewModel habitViewModel = new HabitViewModel();
        habitViewModel.setTitle("habit");
        habitViewModel.setCategory(Category.HEALTH);
        habitViewModel.setPrivate(false);
        habitViewModel.setStartDate(new Date());
        habitViewModel.setToken(token);
        habitViewModel.setCanUsersInvite(true);
        habitViewModel.setFrequency(Frequency.Once);

        habitController.createNewHabit(habitViewModel);
        Habit habitDTO = habitRepository.findByHabitTitle(habitViewModel.getTitle());

        NewTaskViewModel newTaskViewModel = new NewTaskViewModel(token, habitDTO.getId(), "task", 4, Frequency.Once, null, 1);
        TaskDTO taskDTO = taskService.saveTask(newTaskViewModel);

        CompleteTaskDTO completeTaskDTO = new CompleteTaskDTO(habitDTO.getId(), token, taskDTO.getId(), "");
        Post post = taskService.completeTask(completeTaskDTO);

        assertNotNull(post);
    }

}
