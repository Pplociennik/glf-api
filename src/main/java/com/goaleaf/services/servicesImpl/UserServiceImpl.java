package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.ChangeNotificationsViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditImageViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.EditUserViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.LoginViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.PasswordViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.RegisterViewModel;
import com.goaleaf.entities.viewModels.accountsAndAuthorization.SetEmailNotificationsViewModel;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.repositories.UserRepository;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.MemberService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.FileConverter;
import com.goaleaf.validators.UserCredentialsValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.BadCredentialsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.EmailExistsException;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.LoginExistsException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
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
        user.setImageCode("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");
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

        updatedUser.setImageCode(model.imageName);

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
            if (h.getFinished() && h.getWinner().equals("NONE")) {
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

    @Override
    public File uploadProfileImage(MultipartFile multipartFile, String token) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();

        File file = new File("photo");

        try {
            FileUtils.writeByteArrayToFile(file, multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileCode = FileConverter.encodeFileToBase64Binary(file);
        File result = null;

        if (userRepository.findById(Integer.parseInt(claims.getSubject())) == null) {
            throw new RuntimeException("User does not exist!");
        }
        User user = userRepository.findById(Integer.parseInt(claims.getSubject()));
        user.setImageCode(fileCode);
        User response = userRepository.save(user);

        return FileConverter.decodeFileFromBase64Binary(response.getImageCode());

    }

    @Override
    public File getProfilePicture(Integer userID) {

        if (userRepository.findById(userID) == null) {
            throw new RuntimeException("User does not exist!");
        }
        User user = userRepository.findById(userID);

        if (user.getImageCode() == null) {
            throw new RuntimeException("This user has no profile picture!");
        }
        return FileConverter.decodeFileFromBase64Binary(user.getImageCode());
    }

    @Override
    public String getUserImageCode(Integer userID) {
        if (userRepository.findById(userID) == null) {
            throw new RuntimeException("User does not exist!");
        }

        User user = userRepository.findById(userID);
        return user.getImageCode();
    }

    private UserDto convertToDTO(User user) {

        if (user == null) {
            return null;
        }

        UserDto dto = new UserDto();

        dto.setEmailAddress(user.getEmailAddress());
        dto.setImageCode(user.getImageCode());
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
