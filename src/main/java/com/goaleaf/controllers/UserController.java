package com.goaleaf.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.DTO.pagination.HabitPageDTO;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.*;
import com.goaleaf.security.SecurityConstants;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.JwtServiceImpl;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Date;

import static com.goaleaf.security.SecurityConstants.PASSWORD_RECOVERY_SECRET;
import static com.goaleaf.security.SecurityConstants.SECRET;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(value = SecurityConstants.CLIENT_URL, maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtServiceImpl jwtService;


    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<UserDTO> list() {
        return userService.listAllUsers();
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public UserDTO getByPublicId(@PathVariable("id") Integer publicId) {
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
        userService.resetPassword(model);
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
    public UserDTO setEmailNotifications(@RequestBody SetEmailNotificationsViewModel model) throws AccountNotExistsException {
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

    @GetMapping(value = "/finished/paging")
    public HabitPageDTO getFinishedPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam String token) {
        return userService.getFinishedHabitsPaging(pageNr, objectsNr, token);
    }

    @GetMapping(value = "/won/paging")
    public HabitPageDTO getWonPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam String token) {
        return userService.getWonHabitsPaging(pageNr, objectsNr, token);
    }

    @GetMapping(value = "/unfinished/paging")
    public HabitPageDTO getUnfinishedPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam String token) {
        return userService.getUnFinishedHabitsPaging(pageNr, objectsNr, token);
    }
}
