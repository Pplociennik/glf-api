package com.goaleaf.controllers;

import com.auth0.jwt.JWT;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.AuthorizeViewModel;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.servicesImpl.JwtServiceImpl;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.UserCredentialsValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.LoginViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.goaleaf.security.SecurityConstants.EXPIRATION_TIME;
import static com.goaleaf.security.SecurityConstants.SECRET;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserCredentialsValidator userCredentialsValidator;
    @Autowired
    private JwtServiceImpl jwtService;

    @PermitAll
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public UserDto registerUserAccount(@RequestBody RegisterViewModel register) throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException {


        if (!userCredentialsValidator.isValidEmail(register.emailAddress))
            throw new BadCredentialsException("Wrong email format!");
        if (userService.findByEmailAddress(register.emailAddress) != null)
            throw new BadCredentialsException("Account with email " + register.emailAddress + " address already exists!");
        if (userService.findByLogin(register.login) != null)
            throw new LoginExistsException("Account with login " + register.emailAddress + " already exists!");
        if (!userCredentialsValidator.isPasswordFormatValid(register.password))
            throw new BadCredentialsException("Password must be at least 6 characters long and cannot contain spaces!");
        if (!userCredentialsValidator.arePasswordsEquals(register))
            throw new BadCredentialsException("Passwords are not equal!");

        register.password = (bCryptPasswordEncoder.encode(register.password));

        //EmailNotificationsSender sender = new EmailNotificationsSender();

        User user = userService.registerNewUserAccount(register);
        //sender.sayHello(register.emailAddress, register.login);

        UserDto userDto = new UserDto();
        userDto.login = user.getLogin();
        userDto.emailAddress = user.getEmailAddress();
        userDto.userName = user.getUserName();
        userDto.imageName = user.getImageName();
        return userDto;

    }

    @PermitAll
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody LoginViewModel userModel) throws AccountNotExistsException, BadCredentialsException {

        if (userService.findByLogin(userModel.login) == null) {
            throw new AccountNotExistsException("Account with this login not exists!");
        }
        if (!bCryptPasswordEncoder.matches(userModel.password, userService.findByLogin(userModel.login).getPassword())) {
            throw new BadCredentialsException("Wrong Password!!");
        }
        String token = JWT.create()
                .withSubject(String.valueOf(userService.findByLogin(userModel.login).getId()))
//                .withSubject(userService.findByLogin(userModel.login).getLogin())
                .withClaim("Login", userService.findByLogin(userModel.login).getLogin())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        jwtService.Validate(token, SECRET);

        return token;
    }

    @RequestMapping(value = "/validatetoken", method = RequestMethod.POST)
    @CrossOrigin
    public HttpStatus validateToken(@RequestBody AuthorizeViewModel model) throws TimeoutException {
        String token = model.Token;

        if (!jwtService.Validate(token, SECRET))
            throw new TimeoutException("Token expired!");

        return HttpStatus.OK;
    }
}
