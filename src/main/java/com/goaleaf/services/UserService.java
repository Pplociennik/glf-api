package com.goaleaf.services;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.*;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import org.springframework.http.HttpStatus;

import javax.mail.MessagingException;

public interface UserService {

    Iterable<UserDto> listAllUsers();

    User saveUser(User user);

    void removeUser(Integer id);

    Boolean checkIfExists(Integer id);

    UserDto registerNewUserAccount(RegisterViewModel register) throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException;

    UserDto findByLogin(String user);

    UserDto findById(Integer id);

    UserDto updateUser(EditUserViewModel model) throws BadCredentialsException;

    void updateUserImage(EditImageViewModel model);

    UserDto findByEmailAddress(String email);

    Iterable<HabitDTO> getUserFinishedHabits(Integer userID);

    Iterable<HabitDTO> getAllMyWonHabits(Integer userID);

    Iterable<HabitDTO> getAllMyUnfinishedHabits(Integer userID);

    void setNewPassword(PasswordViewModel newPasswords) throws BadCredentialsException;

    HttpStatus disableNotifications(ChangeNotificationsViewModel model) throws AccountNotExistsException;

    UserDto setEmailNotifications(SetEmailNotificationsViewModel model);

    void checkUserCredentials(LoginViewModel userModel) throws AccountNotExistsException, BadCredentialsException;

}
