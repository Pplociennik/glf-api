package com.goaleaf.engine;

import com.goaleaf.controllers.AuthController;
import com.goaleaf.controllers.HabitController;
import com.goaleaf.entities.DTO.CompleteTaskDTO;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;
import com.goaleaf.entities.viewModels.TaskViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.LoginViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.services.TaskService;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.TaskServiceImpl;
import com.goaleaf.services.servicesImpl.UserServiceImpl;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.mail.MessagingException;
import java.util.Date;

@RunWith(SpringRunner.class)
@WebAppConfiguration
public class TaskServiceTests {

    private MockMvc mvc;
    private AuthController authController;
    private TaskService taskServiceImpl;
    private UserService userServiceImpl;
    private HabitController habitController;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Before
    public void PrepareTests() {
        authController = new AuthController();
        taskServiceImpl = new TaskServiceImpl();
        userServiceImpl = new UserServiceImpl();
        habitController = new HabitController();
    }

    @Test
    public void whenTaskCompleted_thenOK() throws BadCredentialsException, MessagingException, AccountNotExistsException {

        RegisterViewModel model = new RegisterViewModel();
        model.login = "tester";
        model.userName = "tester";
        model.emailAddress = "email@email.com";
        model.password = "password";
        model.matchingPassword = "password";

        UserDto dto = userServiceImpl.registerNewUserAccount(model);

        LoginViewModel loginViewModel = new LoginViewModel();
        loginViewModel.login = model.login;
        loginViewModel.password = model.password;

        String token = authController.login(loginViewModel);

        HabitViewModel habitViewModel = new HabitViewModel();
        habitViewModel.title = "habit";
        habitViewModel.category = Category.HEALTH;
        habitViewModel.isPrivate = false;
        habitViewModel.startDate = new Date();
        habitViewModel.token = token;
        habitViewModel.canUsersInvite = true;
        habitViewModel.frequency = Frequency.Once;

        HabitDTO habitDTO = habitController.createNewHabit(habitViewModel);

        TaskDTO taskDTO = new TaskDTO(token, habitDTO.id, "task", 4, Frequency.Once, null, 1);
        TaskViewModel taskViewModel = taskServiceImpl.saveTask(taskDTO);

        CompleteTaskDTO completeTaskDTO = new CompleteTaskDTO(habitDTO.id, token, taskViewModel.getId(), "");
        Post post = taskServiceImpl.completeTask(completeTaskDTO);

        assertNotNull(post);
    }

}
