package com.goaleaf.services;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.*;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;

public interface UserService {

    Iterable<UserDTO> listAllUsers();

    User saveUser(User user);

    void removeUser(Integer id);

    Boolean checkIfExists(Integer id);

    UserDTO registerNewUserAccount(RegisterViewModel register) throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException;

    UserDTO findByLogin(String user);

    UserDTO findById(Integer id);

    UserDTO updateUser(EditUserViewModel model) throws BadCredentialsException;

    void updateUserImage(EditImageViewModel model);

    UserDTO findByEmailAddress(String email);

    Iterable<HabitDTO> getUserFinishedHabits(Integer userID);

    Iterable<HabitDTO> getAllMyWonHabits(Integer userID);

    Iterable<HabitDTO> getAllMyUnfinishedHabits(Integer userID);

    void setNewPassword(PasswordViewModel newPasswords) throws BadCredentialsException;

    HttpStatus disableNotifications(ChangeNotificationsViewModel model) throws AccountNotExistsException;

    UserDTO setEmailNotifications(SetEmailNotificationsViewModel model);

    void checkUserCredentials(LoginViewModel userModel) throws AccountNotExistsException, BadCredentialsException;

    File uploadProfileImage(MultipartFile multipartFile, String token);

    File getProfilePicture(Integer userID);

    String getUserImageCode(Integer userID);

    void resetPassword(EmailViewModel model) throws AccountNotExistsException, MessagingException;
}
