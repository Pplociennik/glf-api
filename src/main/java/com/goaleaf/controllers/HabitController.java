package com.goaleaf.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.Notification;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.habitsCreating.AddMemberViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.DeleteMemberViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.JoinHabitViewModel;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.*;
import com.goaleaf.validators.HabitTitleValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.habitsCreating.NoCategoryException;
import com.goaleaf.validators.exceptions.habitsCreating.NoFrequencyException;
import com.goaleaf.validators.exceptions.habitsCreating.NoPrivacyException;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import com.goaleaf.validators.exceptions.habitsProcessing.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static com.goaleaf.security.SecurityConstants.SECRET;


@RestController
@RequestMapping("/api/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private NotificationService notificationService;

    private HabitTitleValidator habitTitleValidator = new HabitTitleValidator();


    @RequestMapping(method = RequestMethod.POST, value = "/new-habit")
    public HabitDTO createNewHabit(@RequestBody HabitViewModel model) throws WrongTitleException, NoPrivacyException, NoFrequencyException, NoCategoryException {

        if (!habitTitleValidator.isValid(model.title))
            throw new WrongTitleException("Habit's title must be at least 5 and no more than 50 characters long!");
        if (model.category == null)
            throw new NoCategoryException("You have to choose category!");
        if (model.frequency == null)
            throw new NoFrequencyException("You have to choose frequency!");
        if (model.isPrivate == null)
            throw new NoPrivacyException("You have to choose privacy!");
        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in to create a habit!");

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.token).getBody();

        HabitDTO habitDTO = new HabitDTO();
        habitDTO.category = model.category;
        habitDTO.frequency = model.frequency;
//        habitDTO.members = model.members;
        habitDTO.startDate = model.startDate;
        habitDTO.isPrivate = model.isPrivate;
        habitDTO.title = model.title;
        habitDTO.creatorID = Integer.parseInt(claims.getSubject());

        Habit resHabit = new Habit();
        resHabit = habitService.registerNewHabit(model, Integer.parseInt(claims.getSubject()));

        if (resHabit.getWinner() != "NONE") {
            habitDTO.isFinished = true;
            habitDTO.winner = resHabit.getWinner();
        } else {
            habitDTO.isFinished = false;
            habitDTO.winner = "No one yet! :)";
        }

        return habitDTO;
    }

    @PermitAll
    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Iterable<HabitDTO> list(/*String token*/) {

        return habitService.listAllHabits();
    }

    @PermitAll
    @RequestMapping(value = "/getHabit/{id}", method = RequestMethod.GET)
    public HabitDTO getHabitById(Integer id) {
        if (habitService.findById(id) == null)
            throw new HabitNotExistsException("Habit does not exist!");

        return habitService.findById(id);
    }

    @RequestMapping(value = "/invitemember", method = RequestMethod.POST)
    public HttpStatus inviteMemberByLogin(@RequestBody AddMemberViewModel model) throws AccountNotExistsException, MessagingException {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.token).getBody();

        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in to invite users!");
        if (!memberService.checkIfExist(new Member(Integer.parseInt(claims.getSubject()), model.habitID)))
            throw new UserNotInHabitException("You are only allowed to invite users to habits you are involved in!");
        if (habitService.findById(model.habitID) == null)
            throw new HabitNotExistsException("Habit with this id does not exist!");
        if (userService.findByLogin(model.userLogin) == null)
            throw new AccountNotExistsException("User with this login does not exist!");

        User searchingUser = userService.findByLogin(model.userLogin);

        Member newMember = new Member();
        newMember.setUserID(searchingUser.getId());
        newMember.setHabitID(model.habitID);
        newMember.setImgName(searchingUser.getImageName());
        newMember.setUserLogin(searchingUser.getLogin());

        if (memberService.checkIfExist(newMember))
            throw new UserAlreadyInHabitException("User already participating!");

//        memberService.saveMember(newMember);

        Notification ntf = new Notification();
        ntf.setDate(new Date());
        ntf.setRecipientID(searchingUser.getId());
        ntf.setDescription(userService.findById(Integer.parseInt(claims.getSubject())).getLogin() + " invited you to group " + habitService.findById(model.habitID).title + "!");
        ntf.setUrl((model.url.isEmpty() ? "EMPTY_URL" : model.url));
        notificationService.saveNotification(ntf);

        if (searchingUser.getNotifications()) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            sender.sendInvitationNotification(searchingUser.getEmailAddress(), searchingUser.getLogin(), userService.findById(Integer.parseInt(claims.getSubject())).getLogin(), habitService.findById(model.habitID).title);
        }

        return HttpStatus.OK;
    }

    @RequestMapping(value = "/removemember", method = RequestMethod.DELETE)
    public HttpStatus removeMemberFromDatabase(@RequestBody DeleteMemberViewModel model) {
        if (habitService.findById(model.habitID) == null)
            throw new HabitNotExistsException("Habit does not exist!");
        if (memberService.findSpecifiedMember(model.habitID, model.userID) == null)
            throw new MemberDoesNotExistException("Member does not exist!");
        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");

        memberService.removeSpecifiedMember(model.habitID, model.userID);

        if (memberService.countAllHabitMembers(model.habitID) == 0)
            habitService.removeHabit(model.habitID);

        return HttpStatus.OK;
    }

    @RequestMapping(value = "/habit/checkPermissions", method = RequestMethod.GET)
    public boolean checkIfAccessAllowed(@RequestParam Integer userID, @RequestParam Integer habitID) {
        Member memberToCheck = memberService.findSpecifiedMember(habitID, userID);
        Notification notificationToCheck = notificationService.findSpecifiedNtf(userID, "*/habit/" + habitID);

        if (!habitService.findById(habitID).isPrivate)
            return true;

        return memberToCheck != null || notificationToCheck != null;

    }


    @RequestMapping(value = "/habit/members", method = RequestMethod.GET)
    public Iterable<Member> getAllHabitMembers(Integer habitID) {
        if (!habitService.checkIfExists(habitID))
            throw new HabitNotExistsException("Habit does not exist!");

        return memberService.getAllByHabitID(habitID);
    }

    @RequestMapping(value = "/habit/countmembers", method = RequestMethod.GET)
    public Integer countAllHabitMembers(Integer habitID) {
        if (habitService.findById(habitID) == null)
            throw new HabitNotExistsException("Habit with given id does not exist!");

        return memberService.countAllHabitMembers(habitID);
    }

    @RequestMapping(value = "/habit/join", method = RequestMethod.POST)
    public HttpStatus joinHabit(@RequestBody JoinHabitViewModel model) throws AccountNotExistsException {
        if (habitService.findById(model.habitID) == null)
            throw new HabitNotExistsException("Habit does not exist!");
        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (userService.findById(model.userID) == null)
            throw new AccountNotExistsException("Account does not exist!");
//        if (habitService.findById(model.habitID).getPrivate())
//            throw new HabitNotPublicException("You cannot join private habits!");
        if (memberService.findSpecifiedMember(model.habitID, model.userID) != null)
            throw new MemberExistsException("You cannot join habit you are already involved in!");

        User tempUser = userService.getUserById(model.userID);

        Member newMember = new Member();
        newMember.setHabitID(model.habitID);
        newMember.setUserID(model.userID);
        newMember.setImgName(tempUser.getImageName());
        newMember.setUserLogin(tempUser.getLogin());
        newMember.setPoints(0);

        memberService.saveMember(newMember);
        return HttpStatus.OK;
    }

    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public Map<Integer, Member> getHabitMembersRank(@RequestParam Integer habitID) {
        return habitService.getRank(habitID);
    }

    @RequestMapping(value = "/habit/setPointsToWIn", method = RequestMethod.POST)
    public HabitDTO setPointsToWin(@RequestParam Integer habitID, Integer pointsToWin) {
        return habitService.setPointsToWin(habitID,pointsToWin);
    }

}
