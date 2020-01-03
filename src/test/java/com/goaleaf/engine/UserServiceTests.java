package com.goaleaf.engine;

import com.goaleaf.controllers.AuthController;
import com.goaleaf.entities.DTO.UsersDTO;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.LoginViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.PasswordViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import com.goaleaf.repositories.UserRepository;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.mail.MessagingException;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@AutoConfigureMockRestServiceServer
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTests {

    @Autowired
    private AuthController authController;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private Set<User> toClean;

    @Before
    public void prepare() {
        this.toClean = new HashSet<>(0);
    }

    @After
    public void clean() {
        this.toClean.forEach(user -> userRepository.delete(user));
    }

    @Test(expected = BadCredentialsException.class)
    public void whenEmailFormatWrong_thenBadCredentialsException() throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException {

        RegisterViewModel model = new RegisterViewModel();
        model.login = "tester";
        model.emailAddress = "email";
        model.password = "password";
        model.matchingPassword = "password";

        UsersDTO dto = userService.registerNewUserAccount(model);

        this.toClean.add(userRepository.findByLogin(model.login));

    }

    @Test(expected = LoginExistsException.class)
    public void whenLoginExists_thenLoginExistsException() throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException {

        RegisterViewModel model = new RegisterViewModel();
        model.login = "tester";
        model.emailAddress = "email@email.com";
        model.password = "password";
        model.matchingPassword = "password";

        RegisterViewModel model_new = new RegisterViewModel();
        model_new.login = model.login;
        model_new.emailAddress = "2mail@2mail.com";
        model_new.password = "password";
        model_new.matchingPassword = "password";

        UsersDTO dto = userService.registerNewUserAccount(model);

        this.toClean.add(userRepository.findByLogin(dto.getLogin()));

        UsersDTO dto_new = userService.registerNewUserAccount(model_new);

    }

    @Test(expected = BadCredentialsException.class)
    public void whenChangedPasswordsNotEqual_thenBadCredentialsException() throws BadCredentialsException, MessagingException, AccountNotExistsException {

        RegisterViewModel model = new RegisterViewModel();
        model.login = "tester";
        model.emailAddress = "email@email.com";
        model.password = "password";
        model.matchingPassword = "password";

        UsersDTO dto = userService.registerNewUserAccount(model);

        this.toClean.add(userRepository.findByLogin(model.login));

        LoginViewModel loginViewModel = new LoginViewModel();
        loginViewModel.login = model.login;
        loginViewModel.password = model.password;

        String token = authController.login(loginViewModel);

        PasswordViewModel passwordViewModel = new PasswordViewModel();
        passwordViewModel.password = "mypassword";
        passwordViewModel.matchingPassword = "password";

        userService.setNewPassword(passwordViewModel);

    }

}
