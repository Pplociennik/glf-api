package com.goaleaf.controllers;

import com.auth0.jwt.JWT;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.Stats;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.AuthorizeViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.LoginViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import com.goaleaf.services.StatsService;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.JwtServiceImpl;
import com.goaleaf.validators.UserCredentialsValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
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
@CrossOrigin(value = "https://www.goaleaf.com", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserCredentialsValidator userCredentialsValidator;
    @Autowired
    private JwtServiceImpl jwtService;
    @Autowired
    private StatsService statsService;

    @PermitAll
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public UserDTO registerUserAccount(@RequestBody RegisterViewModel register) throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException {

        return userService.registerNewUserAccount(register);

    }

    @PermitAll
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestBody LoginViewModel userModel) throws AccountNotExistsException, BadCredentialsException {

        userService.checkUserCredentials(userModel);

        String token = JWT.create()
                .withSubject(String.valueOf(userService.findByLogin(userModel.login).getUserID()))
//                .withSubject(userService.findByLogin(userModel.login).getLogin())
                .withClaim("Login", userService.findByLogin(userModel.login).getLogin())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(HMAC512(SECRET.getBytes()));
        jwtService.Validate(token, SECRET);

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseLoggedUsers();
        statsService.save(stats);

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
