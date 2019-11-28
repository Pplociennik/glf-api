package com.goaleaf.controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.*;
import com.goaleaf.repositories.UserRepository;
import com.goaleaf.security.EmailSender;
import com.goaleaf.services.UserService;
import com.goaleaf.services.servicesImpl.JwtServiceImpl;
import com.goaleaf.validators.UserCredentialsValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.goaleaf.security.SecurityConstants.*;

@RestController
@RequestMapping("/api/users")

public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtServiceImpl jwtService;
    @Autowired
    private UserCredentialsValidator userCredentialsValidator;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //to listing all users
    @PermitAll
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<User> list(/*String token*/) {
//        jwtService.Validate(token);
//        userService.saveUser(new User(1, "defaultUser", "DefaultUser", "def", "def", "def@default.com"));
        return userService.listAllUsers();
    }

    //to listing all users paging
//    @RequestMapping(value = "/{page}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public Iterable<User> list(@PathVariable("page") Integer pageNr, @RequestParam("size") Optional<Integer> howManyOnPage) {
//        return userService.listAllUsersPaging(pageNr, howManyOnPage.orElse(2));
//    }

    //to getting one specified user by id
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDto getByPublicId(@PathVariable("id") Integer publicId) {
        User tempUser = userService.findById(publicId);
        UserDto dataToReturn = new UserDto();
        dataToReturn.login = tempUser.getLogin();
        dataToReturn.emailAddress = tempUser.getEmailAddress();
        dataToReturn.imageName = tempUser.getImageName();
        dataToReturn.notifications = tempUser.getNotifications();

        return dataToReturn;
    }

    //to getting user by id
//    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//    public User getByParamPublicId(@RequestParam("id") Integer publicId) {
//        return userService.getUserById(publicId);
//    }

    @CrossOrigin("*")
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
                .withSubject(String.valueOf(userService.findByEmailAddress(model.emailAddress).getId()))
                .withClaim("Email", model.emailAddress)
                .withExpiresAt(new Date(System.currentTimeMillis() + PASSWORD_RECOVERY_SECRET_EXPIRATION_TIME))
                .sign(HMAC512(PASSWORD_RECOVERY_SECRET.getBytes()));
        jwtService.Validate(resetPasswordToken, PASSWORD_RECOVERY_SECRET);

        EmailSender sender = new EmailSender();
        sender.setSender("goaleaf@gmail.com", "spaghettiCode");
        sender.addRecipient(model.emailAddress);
        sender.setSubject("GoaLeaf Password Reset Request");
        sender.setBody("Hello " + userService.findByEmailAddress(model.emailAddress).getLogin() + "!\n\n" +
                "Here's your confirmation link: */resetpassword/" + resetPasswordToken + "\n\n" +
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
        Claims claims = Jwts.parser()
                .setSigningKey(PASSWORD_RECOVERY_SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(newPasswords.token).getBody();

        User user = userService.findById(Integer.parseInt(claims.getSubject()));

        if (!userCredentialsValidator.isPasswordFormatValid(newPasswords.password))
            throw new BadCredentialsException("Password must be at least 6 characters long and cannot contain spaces!");
        if (!(newPasswords.password.equals(newPasswords.matchingPassword)))
            throw new BadCredentialsException("Passwords are not equal!");

        user.setPassword(bCryptPasswordEncoder.encode(newPasswords.password));
        userService.saveUser(user);
    }

    @RequestMapping(value = "/user/{login}", method = RequestMethod.GET)
    public UserDto getUserByLogin(String login) {
        User user = userService.findByLogin(login);

        UserDto userDto = new UserDto();
        userDto.userID = user.getId();
        userDto.emailAddress = user.getEmailAddress();
        userDto.login = user.getLogin();

        return userDto;
    }

    @CrossOrigin("*")
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

        User temp = userService.findById(model.userID);
        temp.setNotifications(!temp.getNotifications());
        userRepository.save(temp);

        return HttpStatus.OK;

    }

    @RequestMapping(value = "/setntf", method = RequestMethod.POST)
    public User setEmailNotifications(@RequestBody SetEmailNotificationsViewModel model) throws AccountNotExistsException {
        if (userService.findById(model.userID) == null)
            throw new AccountNotExistsException("User does not exist!");
        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");

        User temp = userService.findById(model.userID);
        temp.setNotifications(model.newNotificationsStatus);
        return userRepository.save(temp);
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
