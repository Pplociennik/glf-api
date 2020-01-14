package com.goaleaf.controllers;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.DTO.NotificationDTO;
import com.goaleaf.entities.DTO.pagination.HabitPageDTO;
import com.goaleaf.entities.DTO.pagination.RankPageDTO;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Sorting;
import com.goaleaf.entities.viewModels.habitsCreating.AddMemberViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.DeleteMemberViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.JoinHabitViewModel;
import com.goaleaf.services.*;
import com.goaleaf.validators.HabitTitleValidator;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.habitsCreating.NoCategoryException;
import com.goaleaf.validators.exceptions.habitsCreating.NoFrequencyException;
import com.goaleaf.validators.exceptions.habitsCreating.NoPrivacyException;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import com.goaleaf.validators.exceptions.habitsProcessing.HabitNotExistsException;
import com.goaleaf.validators.exceptions.habitsProcessing.MemberDoesNotExistException;
import com.goaleaf.validators.exceptions.habitsProcessing.MemberExistsException;
import com.goaleaf.validators.exceptions.habitsProcessing.UserNotInHabitException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
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

        if (!habitTitleValidator.isValid(model.getTitle()))
            throw new WrongTitleException("Habit's title must be at least 5 and no more than 50 characters long!");
        if (model.getCategory() == null)
            throw new NoCategoryException("You have to choose category!");
        if (model.getFrequency() == null)
            throw new NoFrequencyException("You have to choose frequency!");
        if (model.getPrivate() == null)
            throw new NoPrivacyException("You have to choose privacy!");
        if (!jwtService.Validate(model.getToken(), SECRET))
            throw new TokenExpiredException("You have to be logged in to create a habit!");

        return habitService.createNewHabit(model);
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
                .parseClaimsJws(model.getToken()).getBody();

        if (!jwtService.Validate(model.getToken(), SECRET))
            throw new TokenExpiredException("You have to be logged in to invite users!");
        if (!memberService.checkIfExist(new Member(Integer.parseInt(claims.getSubject()), model.getHabitID())))
            throw new UserNotInHabitException("You are only allowed to invite users to habits you are involved in!");
        if (habitService.findById(model.getHabitID()) == null)
            throw new HabitNotExistsException("Habit with this id does not exist!");
        if (userService.findByLogin(model.getUserLogin()) == null)
            throw new AccountNotExistsException("User with this login does not exist!");

        return habitService.inviteNewMember(model);

    }

    @RequestMapping(value = "/removemember", method = RequestMethod.DELETE)
    public HttpStatus removeMemberFromDatabase(@RequestBody DeleteMemberViewModel model) {
        if (habitService.findById(model.getHabitID()) == null)
            throw new HabitNotExistsException("Habit does not exist!");
        if (memberService.findSpecifiedMember(model.getHabitID(), model.getUserID()) == null)
            throw new MemberDoesNotExistException("Member does not exist!");
        if (!jwtService.Validate(model.getToken(), SECRET))
            throw new TokenExpiredException("You have to be logged in!");

        memberService.removeSpecifiedMember(model.getHabitID(), model.getUserID());

        if (memberService.countAllHabitMembers(model.getHabitID()) == 0)
            habitService.deleteHabit(model.getHabitID(), model.getToken());

        return HttpStatus.OK;
    }

    @RequestMapping(value = "/habit/checkPermissions", method = RequestMethod.GET)
    public boolean checkIfAccessAllowed(@RequestParam Integer userID, @RequestParam Integer habitID) {
        MemberDTO memberToCheck = memberService.findSpecifiedMember(habitID, userID);
        NotificationDTO notificationToCheck = notificationService.findSpecifiedNtf(userID, "*/habit/" + habitID);

        if (!habitService.findById(habitID).getPrivate()) {
            if (memberToCheck != null && memberToCheck.getBanned()) {
                return false;
            }
            return true;
        }

        return memberToCheck != null || notificationToCheck != null;

    }


    @RequestMapping(value = "/habit/members", method = RequestMethod.GET)
    public Iterable<MemberDTO> getAllHabitMembers(Integer habitID) {
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
        if (habitService.findById(model.getHabitID()) == null)
            throw new HabitNotExistsException("Habit does not exist!");
        if (!jwtService.Validate(model.getToken(), SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (userService.findById(model.getUserID()) == null)
            throw new AccountNotExistsException("Account does not exist!");
//        if (habitService.findById(model.habitID).getPrivate())
//            throw new HabitNotPublicException("You cannot join private habits!");
        if (memberService.findSpecifiedMember(model.getHabitID(), model.getUserID()) != null)
            throw new MemberExistsException("You cannot join habit you are already involved in!");

        return habitService.joinHabit(model);
    }

    @RequestMapping(value = "/rank", method = RequestMethod.GET)
    public Map<Integer, MemberDTO> getHabitMembersRank(@RequestParam Integer habitID) {
        return habitService.getRank(habitID);
    }

    @RequestMapping(value = "/habit/setPointsToWIn", method = RequestMethod.POST)
    public HabitDTO setPointsToWin(@RequestParam Integer habitID, Integer pointsToWin) {
        return habitService.setPointsToWin(habitID, pointsToWin);
    }

    @RequestMapping(value = "/habit/setInvitingPermissions", method = RequestMethod.POST)
    public Boolean setInvitingPermissions(@RequestParam Boolean allowed, Integer habitID) {
        return habitService.setInvitingPermissions(allowed, habitID);
    }

    @RequestMapping(value = "/habit/remove", method = RequestMethod.DELETE)
    public HttpStatus deleteHabit(@RequestParam Integer habitID, String token) {
        return habitService.deleteHabit(habitID, token);
    }

    @RequestMapping(value = "/all/by-category", method = RequestMethod.GET)
    public Iterable<HabitDTO> getAllHabitsSortedByCategory(@RequestParam Category category) {
        return habitService.getAllHabitsByCategory(category);
    }

    @RequestMapping(value = "/all/sorted", method = RequestMethod.GET)
    public Iterable<HabitDTO> getAllHabitsByDateOrPopularity(@RequestParam Sorting sorting) {
        return habitService.getAllHabitsBySorting(sorting);
    }

    @RequestMapping(value = "/ban", method = RequestMethod.POST)
    public MemberDTO banAMember(@RequestParam Integer userID, Integer habitID) {
        return memberService.banAMember(userID, habitID);
    }

    @RequestMapping(value = "/ban/check", method = RequestMethod.GET)
    public Boolean checkIfMemberBanned(@RequestParam Integer userID, Integer habitID) {
        return memberService.checkIfUserIsBanned(userID, habitID);
    }

    @RequestMapping(value = "/privacy/change", method = RequestMethod.POST)
    public Boolean changePrivacy(Integer habitID) {
        return habitService.changeHabitPrivacy(habitID);
    }

    @PostMapping(value = "/category/change")
    public Category changeHabitCategory(@RequestParam Integer habitID, Category category) {
        return habitService.changeHabitCategory(habitID, category);
    }

    @GetMapping(value = "/all/paging")
    public HabitPageDTO getAllHabitsPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Category category, @RequestParam Sorting sorting) {
        return habitService.listAllHabitsPaging(pageNr, objectsNr, category, sorting);
    }

    @GetMapping(value = "/category/paging")
    public HabitPageDTO getByCategoryPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Category category) {
        return habitService.getAllByCategoryPaging(pageNr, objectsNr, category);
    }

    @GetMapping(value = "/rank/paging")
    public RankPageDTO getMembersRankingPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Integer habitID) {
        return memberService.getHabitRankingPaging(pageNr, objectsNr, habitID);
    }

    @PutMapping(value = "/name/change")
    public HabitDTO changeHabitName(@RequestParam Integer habitID, @RequestParam String newName) {
        return habitService.updateHabitName(habitID, newName);
    }

    @PutMapping(value = "/discussion/change")
    public Boolean changeDiscussionPermissions(@RequestParam Integer habitID) {
        return habitService.changeDiscussionPermissions(habitID);
    }

}
