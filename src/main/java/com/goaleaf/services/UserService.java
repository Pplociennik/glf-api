package com.goaleaf.services;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.User;

import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditImageViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditUserViewModel;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    Iterable<User> listAllUsers();

    User getUserById(Integer id);

    User saveUser(User user);

    void removeUser(Integer id);

    Boolean checkIfExists(Integer id);

    Iterable<User> listAllUsersPaging(Integer pageNr, Integer howManyOnPage);

    User registerNewUserAccount(RegisterViewModel register) throws EmailExistsException, LoginExistsException;

    User findByLogin(String user);

    User findById(Integer id);

    void updateUser(EditUserViewModel model) throws BadCredentialsException;

    void updateUserImage(EditImageViewModel model);

    User findByEmailAddress(String email);

    Iterable<HabitDTO> getUserFinishedHabits(Integer userID);

    Iterable<HabitDTO> getAllMyWonHabits(Integer userID);

    Iterable<HabitDTO> getAllMyUnfinishedHabits(Integer userID);

    }
