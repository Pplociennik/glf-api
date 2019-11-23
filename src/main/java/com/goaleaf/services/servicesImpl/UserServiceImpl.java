package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditImageViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditUserViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.repositories.RoleRepository;
import com.goaleaf.repositories.UserRepository;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.MemberService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.UserCredentialsValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private HabitService habitService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;

    private UserCredentialsValidator userCredentialsValidator = new UserCredentialsValidator();

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Iterable<User> listAllUsersPaging(Integer pageNr, Integer howManyOnPage) {
        return userRepository.findAll(new PageRequest(pageNr, howManyOnPage));
    }

    @Override
    public Iterable<User> listAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void removeUser(Integer id) {
        userRepository.delete(id);

        Iterable<Habit> userHabits = habitService.findHabitsByCreatorID(id);

        for (Habit habit : userHabits) {
            memberService.removeSpecifiedMember(habit.getId(), habit.getCreatorID());
            habit.setCreatorID(null);
            habit.setCreatorLogin(habit.getCreatorLogin() + "(ACCOUNT_NOT_EXISTS)");
        }
    }

    @Override
    public Boolean checkIfExists(Integer id) {
        if (userRepository.checkIfExists(id) > 0)
            return true;
        else
            return false;
    }

    @Transactional
    @Override
    public User registerNewUserAccount(RegisterViewModel register)
            throws EmailExistsException, LoginExistsException {


        User user = new User();
        user.setLogin(register.login);
        user.setUserName(register.userName);
        user.setPassword(register.password);
        user.setEmailAddress(register.emailAddress);
        user.setImageName("def_goaleaf_avatar.png");
        return userRepository.save(user);
    }

    public void updateUser(EditUserViewModel model) throws BadCredentialsException {
        if (findById(model.id) != null) {
            User updatingUser = findById(model.id);


            if (bCryptPasswordEncoder.matches(model.oldPassword, userRepository.findById(model.id).getPassword())) {
                if (!model.emailAddress.isEmpty()) {
                    if (!userCredentialsValidator.isValidEmail(model.emailAddress)) {
                        throw new BadCredentialsException("Wrong email format!");
                    } else {
                        updatingUser.setEmailAddress(model.emailAddress);
                    }
                }
                if (model.newPassword.equals(model.matchingNewPassword)) {
                    if (!userCredentialsValidator.isPasswordFormatValid(model.newPassword)) {
                        throw new BadCredentialsException("Password must be at least 6 characters long and cannot contain spaces!");
                    } else {
                        updatingUser.setPassword(bCryptPasswordEncoder.encode(model.newPassword));
                    }
                } else
                    throw new BadCredentialsException("Passwords are not equal!");
            } else {
                throw new BadCredentialsException("Wrong Password!");
            }

            userRepository.save(updatingUser);
        }
    }

    public void updateUserImage(EditImageViewModel model) {
        User updatedUser = findById(model.id);

        updatedUser.setImageName(model.imageName);

        saveUser(updatedUser);
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public User findByEmailAddress(String email) {
        return userRepository.findByEmailAddress(email);
    }

    @Override
    public Iterable<HabitDTO> getUserFinishedHabits(Integer userID) {
        Iterable<Member> memberList = memberRepository.findAllByUserID(userID);
        List<Habit> habits = new ArrayList<>(0);

        for (Member m : memberList) {
            Habit h = new Habit();
            h = habitService.getHabitById(m.getHabitID());
            if (h.getFinished()) {
                habits.add(h);
            }
        }

        Iterable<HabitDTO> result = habitService.convertManyToDTOs(habits);
        return result;
    }

    @Override
    public Iterable<HabitDTO> getAllMyWonHabits(Integer userID) {
        User user = userRepository.findById(userID);
        Iterable<Member> members = memberRepository.findAllByUserID(userID);
        List<Habit> habits = new ArrayList<>(0);

        for (Member m : members) {
            Habit h = new Habit();
            h = habitService.getHabitById(m.getHabitID());
            if (h.getWinner().equals(user.getLogin())) {
                habits.add(h);
            }
        }

        Iterable<HabitDTO> result = habitService.convertManyToDTOs(habits);
        return result;
    }

    @Override
    public Iterable<HabitDTO> getAllMyUnfinishedHabits(Integer userID) {
        Iterable<Member> memberList = memberRepository.findAllByUserID(userID);
        List<Habit> habits = new ArrayList<>(0);

        for (Member m : memberList) {
            Habit h = new Habit();
            h = habitService.getHabitById(m.getHabitID());
            if (!h.getFinished()) {
                habits.add(h);
            }
        }

        Iterable<HabitDTO> result = habitService.convertManyToDTOs(habits);
        return result;
    }
}
