package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.*;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.repositories.UserRepository;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.MemberService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.UserCredentialsValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.goaleaf.security.SecurityConstants.PASSWORD_RECOVERY_SECRET;
import static com.goaleaf.security.SecurityConstants.SECRET;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
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
    public Iterable<UserDto> listAllUsers() {
        return convertManyToDTOs(userRepository.findAll());
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
    public UserDto registerNewUserAccount(RegisterViewModel register)
            throws EmailExistsException, LoginExistsException, BadCredentialsException, MessagingException {

        if (!userCredentialsValidator.isValidEmail(register.emailAddress))
            throw new BadCredentialsException("Wrong email format!");
        if (userRepository.findByEmailAddress(register.emailAddress) != null)
            throw new BadCredentialsException("Account with email " + register.emailAddress + " address already exists!");
        if (userRepository.findByLogin(register.login) != null)
            throw new LoginExistsException("Account with login " + register.login + " already exists!");
        if (!userCredentialsValidator.isLoginLengthValid(register.login))
            throw new BadCredentialsException("Login cannot be longer than 20 characters!");
        if (!userCredentialsValidator.isPasswordFormatValid(register.password))
            throw new BadCredentialsException("Password must be at least 6 characters long and cannot contain spaces!");
        if (!userCredentialsValidator.arePasswordsEquals(register))
            throw new BadCredentialsException("Passwords are not equal!");

        register.password = (bCryptPasswordEncoder.encode(register.password));

        EmailNotificationsSender sender = new EmailNotificationsSender();

        sender.sayHello(register.emailAddress, register.login);

        User user = new User();
        user.setLogin(register.login);
        user.setPassword(register.password);
        user.setEmailAddress(register.emailAddress);
        user.setImageName("def_goaleaf_avatar.png");
        user.setNotifications(true);
        return convertToDTO(userRepository.save(user));
    }

    public UserDto updateUser(EditUserViewModel model) throws BadCredentialsException {

        User updated = new User();

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.token).getBody();

        if (findById(Integer.parseInt(claims.getSubject())) != null) {
            User updatingUser = userRepository.findById(Integer.parseInt(claims.getSubject()));


            if (bCryptPasswordEncoder.matches(model.oldPassword, userRepository.findById(Integer.parseInt(claims.getSubject())).getPassword())) {
//                if (!model.emailAddress.isEmpty()) {
//                    if (!userCredentialsValidator.isValidEmail(model.emailAddress)) {
//                        throw new BadCredentialsException("Wrong email format!");
//                    } else {
//                        updatingUser.setEmailAddress(model.emailAddress);
//                    }
//                }
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

            updated = userRepository.save(updatingUser);
        }
        return convertToDTO(updated);
    }

    public void updateUserImage(EditImageViewModel model) {
        User updatedUser = userRepository.findById(model.id);

        updatedUser.setImageName(model.imageName);

        saveUser(updatedUser);
    }

    @Override
    public UserDto findByLogin(String login) {
        return convertToDTO(userRepository.findByLogin(login));
    }

    @Override
    public UserDto findById(Integer id) {
        return convertToDTO(userRepository.findById(id));
    }

    @Override
    public UserDto findByEmailAddress(String email) {
        return convertToDTO(userRepository.findByEmailAddress(email));
    }

    @Override
    public Iterable<HabitDTO> getUserFinishedHabits(Integer userID) {
        Iterable<Member> memberList = memberRepository.findAllByUserID(userID);
        User user = userRepository.findById(userID);
        List<Habit> habits = new ArrayList<>(0);

        for (Member m : memberList) {
            Habit h = new Habit();
            h = habitService.getHabitById(m.getHabitID());
            if (h.getFinished() && !h.getWinner().equals(user.getLogin())) {
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

    @Override
    public void setNewPassword(PasswordViewModel newPasswords) throws BadCredentialsException {

        Claims claims = Jwts.parser()
                .setSigningKey(PASSWORD_RECOVERY_SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(newPasswords.token).getBody();

        User user = userRepository.findById(Integer.parseInt(claims.getSubject()));

        if (!userCredentialsValidator.isPasswordFormatValid(newPasswords.password))
            throw new BadCredentialsException("Password must be at least 6 characters long and cannot contain spaces!");
        if (!(newPasswords.password.equals(newPasswords.matchingPassword)))
            throw new BadCredentialsException("Passwords are not equal!");

        user.setPassword(bCryptPasswordEncoder.encode(newPasswords.password));
        saveUser(user);
    }

    @Override
    public HttpStatus disableNotifications(ChangeNotificationsViewModel model) {

        User temp = userRepository.findById(model.userID);
        temp.setNotifications(!temp.getNotifications());
        userRepository.save(temp);

        return HttpStatus.OK;
    }

    @Override
    public UserDto setEmailNotifications(SetEmailNotificationsViewModel model) {
        User temp = userRepository.findById(model.userID);
        temp.setNotifications(model.newNotificationsStatus);
        return convertToDTO(userRepository.save(temp));
    }

    @Override
    public void checkUserCredentials(LoginViewModel userModel) throws AccountNotExistsException, BadCredentialsException {
        if (userRepository.findByLogin(userModel.login) == null) {
            throw new AccountNotExistsException("Account with this login not exists!");
        }
        if (!bCryptPasswordEncoder.matches(userModel.password, userRepository.findByLogin(userModel.login).getPassword())) {
            throw new BadCredentialsException("Wrong Password!!");
        }
    }

    private UserDto convertToDTO(User user) {
        UserDto dto = new UserDto();

        dto.setEmailAddress(user.getEmailAddress());
        dto.setImageName(user.getImageName());
        dto.setLogin(user.getLogin());
        dto.setNotifications(user.getNotifications());
        dto.setUserID(user.getId());

        return dto;
    }

    private Iterable<UserDto> convertManyToDTOs(Iterable<User> input) {
        List<UserDto> out = new ArrayList<>(0);

        for (User u : input) {
            out.add(convertToDTO(u));
        }

        Iterable result = out;
        return result;
    }
}
