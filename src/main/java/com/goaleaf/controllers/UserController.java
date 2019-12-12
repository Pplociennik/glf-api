package com.goaleaf.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.*;
import com.goaleaf.security.EmailSender;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.JwtServiceImpl;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.File;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.goaleaf.security.SecurityConstants.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtServiceImpl jwtService;


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<UserDto> list() {
        return userService.listAllUsers();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public UserDto getByPublicId(@PathVariable("id") Integer publicId) {
        return userService.findById(publicId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/edit")
    public void updateUser(@RequestBody EditUserViewModel model) throws BadCredentialsException {

        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");

        userService.updateUser(model);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/resetpassword")
    public void resetPassword(@RequestBody EmailViewModel model) throws AccountNotExistsException, MessagingException {
        if (userService.findByEmailAddress(model.emailAddress) == null)
            throw new AccountNotExistsException("Account with this email address does not exist!");

        String resetPasswordToken = JWT.create()
                .withSubject(String.valueOf(userService.findByEmailAddress(model.emailAddress).getUserID()))
                .withClaim("Email", model.emailAddress)
                .withExpiresAt(new Date(System.currentTimeMillis() + PASSWORD_RECOVERY_SECRET_EXPIRATION_TIME))
                .sign(HMAC512(PASSWORD_RECOVERY_SECRET.getBytes()));
        jwtService.Validate(resetPasswordToken, PASSWORD_RECOVERY_SECRET);

        EmailSender sender = new EmailSender();
        sender.setSender("goaleaf@gmail.com", "spaghettiCode");
        sender.addRecipient(model.emailAddress);
        sender.setSubject("GoaLeaf Password Reset Request");
        sender.setBody("Hello " + userService.findByEmailAddress(model.emailAddress).getLogin() + "!\n\n" +
                "Here's your confirmation link: http://goaleaf.com/resetpassword/" + resetPasswordToken + "\n\n" +
                "If you have not requested a password reset, ignore this message.\n\n" +
                "Thank you and have a nice day! :)\n\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/requestpasswordvalidate")
    public HttpStatus validateRequestPasswordToken(String token) throws BadCredentialsException {
        Date currentDate = new Date();

        if (!jwtService.Validate(token, PASSWORD_RECOVERY_SECRET))
            throw new BadCredentialsException("Account not exists!");
        else
            return HttpStatus.OK;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/setnewpassword")
    public void setNewUserPassword(@RequestBody PasswordViewModel newPasswords) throws BadCredentialsException {
        userService.setNewPassword(newPasswords);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
    public HttpStatus removeUserFromDatabase(@PathVariable Integer id) {
        userService.removeUser(id);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/changentf", method = RequestMethod.PUT)
    public HttpStatus disableUserNotifications(@RequestBody ChangeNotificationsViewModel model) throws AccountNotExistsException {
        if (userService.findById(model.userID) == null)
            throw new AccountNotExistsException("User does not exist!");
        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");

        return userService.disableNotifications(model);

    }

    @RequestMapping(value = "/setntf", method = RequestMethod.POST)
    public UserDto setEmailNotifications(@RequestBody SetEmailNotificationsViewModel model) throws AccountNotExistsException {
        if (userService.findById(model.userID) == null)
            throw new AccountNotExistsException("User does not exist!");
        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");

        return userService.setEmailNotifications(model);
    }

    @RequestMapping(value = "/myFinishedHabits", method = RequestMethod.GET)
    public Iterable<HabitDTO> getMyFinishedHabits(@RequestParam Integer userID) {
        return userService.getUserFinishedHabits(userID);
    }

    @RequestMapping(value = "/myWonHabits", method = RequestMethod.GET)
    public Iterable<HabitDTO> getMyWonHabits(@RequestParam Integer userID) {
        return userService.getAllMyWonHabits(userID);
    }

    @RequestMapping(value = "/myUnfinishedHabits", method = RequestMethod.GET)
    public Iterable<HabitDTO> getMyUnfinishedHabits(@RequestParam Integer userID) {
        return userService.getAllMyUnfinishedHabits(userID);
    }

}
