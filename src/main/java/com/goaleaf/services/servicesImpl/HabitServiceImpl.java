package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.*;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.DTO.pagination.HabitPageDTO;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Sorting;
import com.goaleaf.entities.viewModels.habitsCreating.AddMemberViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.JoinHabitViewModel;
import com.goaleaf.repositories.*;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.*;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import com.goaleaf.validators.exceptions.habitsProcessing.BadGoalValueException;
import com.goaleaf.validators.exceptions.habitsProcessing.UserAlreadyInHabitException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.goaleaf.security.SecurityConstants.SECRET;

@Service
public class HabitServiceImpl implements HabitService {

    @Autowired
    private HabitRepository habitRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private UserService userService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReactionRepository reactionRepository;
    @Autowired
    private TaskHistoryRepository taskHistoryRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private StatsService statsService;


    @Override
    public Iterable<HabitDTO> listAllHabits() {
        Iterable<Habit> input = habitRepository.findAll();
        return convertManyToDTOs(input, true);
    }

    @Override
    public Habit getHabitById(Integer id) {
        return habitRepository.findById(id);
    }

    @Override
    public Habit saveHabit(Habit habit) {
        return habitRepository.save(habit);
    }

    @Override
    public void removeHabit(Integer id) {
        habitRepository.delete(id);
    }

    @Override
    public Boolean checkIfExists(Integer id) {
        return (habitRepository.checkIfExists(id) > 0);
    }

    @Override
    public HabitPageDTO listAllHabitsPaging(Integer pageNr, Integer howManyOnPage, Category category, Sorting sorting) {
        Pageable pageable = new PageRequest(pageNr, howManyOnPage);
        Iterable<Habit> input = habitRepository.findAllByCategoryOrderByHabitStartDateDesc(category);
        if (category.equals(Category.ALL)) {
            input = habitRepository.findAll();
        }

        Iterable<HabitDTO> dtos = (List<HabitDTO>) convertManyToDTOs(input, true);

        List<HabitDTO> sortedList = new ArrayList<>(0);

        if (sorting.equals(Sorting.Popular)) {
            for (HabitDTO h : dtos) {
                sortedList.add(h);
            }
            Collections.sort(sortedList, new Comparator<HabitDTO>() {
                @Override
                public int compare(HabitDTO a, HabitDTO b) {
                    return b.getMembersCount().compareTo(a.getMembersCount());
                }
            });
        } else {
            for (HabitDTO h : dtos) {
                sortedList.add(h);
            }
            Collections.sort(sortedList, new Comparator<HabitDTO>() {
                @Override
                public int compare(HabitDTO a, HabitDTO b) {
                    return b.getStartDate().compareTo(a.getStartDate());
                }
            });
        }

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > sortedList.size() ? sortedList.size() : (start + pageable.getPageSize());
        Page<HabitDTO> pages = new PageImpl<HabitDTO>(sortedList.subList(start, end), pageable, sortedList.size());


        return new HabitPageDTO(pages.getContent(), pages.getNumber(), pages.hasPrevious(), pages.hasNext(), pages.getTotalPages());
    }

    @Override
    public Habit registerNewHabit(HabitViewModel model, Integer creatorID) throws WrongTitleException {

        Habit newHabit = new Habit();

        newHabit.setHabitStartDate(model.getStartDate() == null ? new Date() : model.getStartDate());
        newHabit.setFrequency(model.getFrequency());
        newHabit.setHabitTitle(model.getTitle());
        newHabit.setCategory(model.getCategory());
        newHabit.setPrivate(model.getPrivate());
        newHabit.setCreatorID(creatorID);
        newHabit.setCreatorLogin(userService.findById(creatorID).getLogin());
        newHabit.setWinner("NONE");
        newHabit.setPointsToWIn(1001);
        newHabit.setCanUsersInvite(model.getCanUsersInvite() == null ? true : model.getCanUsersInvite());
        newHabit.setFinished(false);

        Habit added = new Habit();
        added = habitRepository.save(newHabit);

        UserDTO creatorUser = userService.findById(creatorID);

        Member creator = new Member();
        creator.setUserID(creatorID);
        creator.setHabitID(added.getId());
        creator.setUserLogin(creatorUser.getLogin());
        creator.setImageCode(creatorUser.getImageCode());
        creator.setPoints(0);

        String ntfDesc = "Challenge: \"" + newHabit.getHabitTitle() + "\" has been created";
        Notification ntf = new EmailNotificationsSender().createInAppNotification(creatorID, ntfDesc, "http://www.goaleaf.com/challenge/" + added.getId(), false);
        if (creatorUser.getNotifications()) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            try {
                sender.challengeCreated(creatorUser.getEmailAddress(), creatorUser.getLogin(), added);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseCreatedChallenges();
        statsService.save(stats);

        memberService.saveMember(creator);

        return added;
    }

    @Override
    public Habit findByTitle(String title) {
        return null;
    }

    @Override
    public HabitDTO findById(Integer id) {
        return convertToDTO(habitRepository.findById(id));
    }

    @Override
    public Habit findByOwnerName(String ownerName) {
        return null;
    }

    @Override
    public Iterable<Habit> findHabitsByCreatorID(Integer creatorID) {
        return habitRepository.findAllByCreatorID(creatorID);
    }

    @Override
    public Map<Integer, MemberDTO> getRank(Integer habitID) {
        return memberService.getRank(habitID);
    }

    public HabitDTO convertToDTO(Habit entry) {

        if (entry == null) {
            return null;
        }

        UserDTO creator = userService.findById(entry.getCreatorID());

        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setId(entry.getId());
        habitDTO.setCategory(entry.getCategory());
        habitDTO.setFrequency(entry.getFrequency());
//        habitDTO.members = model.members;
        habitDTO.setStartDate(entry.getHabitStartDate());
        habitDTO.setPrivate(entry.getPrivate());
        habitDTO.setTitle(entry.getHabitTitle());
        habitDTO.setCreatorID(entry.getCreatorID());
        habitDTO.setCreatorLogin(creator.getLogin());
        habitDTO.setMembersCount(memberService.countAllHabitMembers(entry.getId()));
        habitDTO.setCanUsersInvite(entry.getCanUsersInvite());

        if (entry.getPointsToWIn() != 1001) {
            habitDTO.setPointsToWin(entry.getPointsToWIn());
        } else {
            habitDTO.setPointsToWin(0);
        }

        if (!entry.getWinner().equals("NONE")) {
            habitDTO.setFinished(true);
            habitDTO.setWinner(entry.getWinner());
        } else {
            habitDTO.setFinished(false);
            habitDTO.setWinner("NONE");
        }

        return habitDTO;
    }

    @Override
    public HabitDTO updateHabitName(Integer habitID, String newName) {
        Habit habit = habitRepository.findById(habitID);
        habit.setHabitTitle(newName);
        return convertToDTO(habitRepository.save(habit));
    }

    public Iterable<HabitDTO> convertManyToDTOs(Iterable<Habit> habits, boolean filterPrivacy) {
        List<HabitDTO> resultList = new ArrayList<>(0);

        for (Habit h : habits) {
            if (filterPrivacy && h.getPrivate()) {
                continue;
            }
            HabitDTO dto = new HabitDTO();
            dto = convertToDTO(h);
            resultList.add(dto);
        }

        Iterable<HabitDTO> result = resultList;
        return result;
    }

    @Override
    public HabitDTO setPointsToWin(Integer habitID, Integer pointsToWin) {

        if (pointsToWin < 1 || pointsToWin > 1000) {
            throw new BadGoalValueException("Goal value has to be > 0 and <= 1000!");
        }

        Habit habit = habitRepository.findById(habitID);

        habit.setPointsToWIn(pointsToWin);

        HabitDTO result = new HabitDTO();
        result = convertToDTO(habitRepository.save(habit));

        Iterable<Member> members = memberRepository.findAllByHabitID(habitID);

        String ntfDesc = "The goal in the challenge \"" + habit.getHabitTitle() + "\" has been updated!";
        for (Member m : members) {
            UserDTO u = userService.findById(m.getUserID());
            Notification ntf = new EmailNotificationsSender().createInAppNotification(m.getUserID(), ntfDesc, "http://www.goaleaf.com/challenge/" + habitID, false);
            if (u.getNotifications()) {
                EmailNotificationsSender sender = new EmailNotificationsSender();
                //sender.goalUpdated(u.getEmailAddress(), u.getLogin(), habit);
            }
        }

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseSetGoals();
        statsService.save(stats);

        return result;
    }

    @Override
    public Boolean setInvitingPermissions(Boolean allowed, Integer habitID) {
        Habit habit = habitRepository.findById(habitID);

        habit.setCanUsersInvite(allowed);

        Habit response = habitRepository.save(habit);
        return response.getCanUsersInvite();
    }

    @Override
    public HttpStatus inviteNewMember(AddMemberViewModel model) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.getToken()).getBody();

        UserDTO inviter = userService.findById(Integer.parseInt(claims.getSubject()));
        Habit habit = habitRepository.findById(model.getHabitID());

        if (!habit.getCanUsersInvite()) {
            if (!habit.getCreatorLogin().equals(inviter.getLogin())) {
                throw new RuntimeException("You are not allowed to invite members!");
            }
        }

        UserDTO searchingUser = userService.findByLogin(model.getUserLogin());

        Member newMember = new Member();
        newMember.setUserID(searchingUser.getUserID());
        newMember.setHabitID(model.getHabitID());
        newMember.setImageCode(searchingUser.getImageCode());
        newMember.setUserLogin(searchingUser.getLogin());
        newMember.setPoints(0);

        Member invitingMember = memberRepository.findByHabitIDAndUserID(model.getHabitID(), searchingUser.getUserID());

        if (invitingMember != null && !invitingMember.getBanned()) {
            throw new UserAlreadyInHabitException("User already participating!");
        }

        if (invitingMember != null && invitingMember.getBanned()) {
            if (inviter.getUserID().compareTo(habit.getCreatorID()) != 0) {
                throw new RuntimeException("User is banned in this challenge!");
            } else {
                invitingMember.setBanned(false);
                memberService.saveMember(invitingMember);
            }
        }

//        memberService.saveMember(newMember);

        String ntfDesc = userService.findById(Integer.parseInt(claims.getSubject())).getLogin() + " invited you to challenge \"" + findById(model.getHabitID()).getTitle() + "\"!";
        Notification ntf = new EmailNotificationsSender().createInAppNotification(searchingUser.getUserID(), ntfDesc, (model.getUrl().isEmpty() ? "EMPTY_URL" : model.getUrl()), true);
        if (searchingUser.getNotifications()) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            try {
                sender.sendInvitationNotification(searchingUser.getEmailAddress(), searchingUser.getLogin(), userService.findById(Integer.parseInt(claims.getSubject())).getLogin(), findById(model.getHabitID()).getTitle());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseInvitedMembers();
        statsService.save(stats);

        return HttpStatus.OK;
    }

    @Override
    public HttpStatus deleteHabit(Integer habitID, String token) {

        if (habitRepository.findById(habitID) == null) {
            return HttpStatus.NOT_FOUND;
        }

        Habit toDelete = habitRepository.findById(habitID);
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();

        if (toDelete.getCreatorID() != Integer.parseInt(claims.getSubject())) {
            throw new RuntimeException("Deleting of a challenge may only be performed by its creator!");
        }

        Iterable<Post> postsList = postRepository.getAllByHabitIDOrderByDateOfAdditionDesc(habitID);

        List<Comment> commentsArrayList = new ArrayList<>(0);
        List<PostReaction> reactionsArrayList = new ArrayList<>(0);

        if (postsList.iterator().hasNext()) {
            for (Post p : postsList) {
                Iterable<Comment> cmds = commentRepository.getAllByPostIDOrderByCreationDateDesc(p.getId());
                cmds.forEach(commentsArrayList::add);
            }
            for (Post p : postsList) {
                Iterable<PostReaction> pstr = reactionRepository.getAllByPostID(p.getId());
                pstr.forEach(reactionsArrayList::add);
            }
        }

        Iterable<Comment> commentsList = commentsArrayList;
        Iterable<PostReaction> reactionsList = reactionsArrayList;
        Iterable<TasksHistoryEntity> tasksHistoryEntities = taskHistoryRepository.findAllByHabitID(habitID);
        Iterable<Task> tasksList = taskRepository.getAllByHabitID(habitID);
        Iterable<Member> membersList = memberRepository.findAllByHabitID(habitID);

        String ntfDesc = "Challenge \"" + toDelete.getHabitTitle() + "\" is no longer available!";
        for (Member m : membersList) {
            UserDTO u = userService.findById(m.getUserID());
            Notification ntf = new EmailNotificationsSender().createInAppNotification(m.getUserID(), ntfDesc, null, false);
            if (u.getNotifications()) {
                EmailNotificationsSender sender = new EmailNotificationsSender();
                try {
                    sender.challengeDeleted(u.getEmailAddress(), u.getLogin(), toDelete);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }

        if (commentsList.iterator().hasNext()) {
            commentRepository.delete(commentsList);
            for (Post p : postsList) {
                if (commentRepository.getAllByPostIDOrderByCreationDateDesc(p.getId()).iterator().hasNext()) {
                    throw new RuntimeException("Comments were not deleted properly! POST_ID: " + p.getId());
                }
            }
        }

        if (reactionsList.iterator().hasNext()) {
            reactionRepository.delete(reactionsList);
            for (Post p : postsList) {
                if (reactionRepository.getAllByPostID(p.getId()).iterator().hasNext()) {
                    throw new RuntimeException("Reactions were not deleted properly! POST_ID: " + p.getId());
                }
            }
        }

        if (postsList.iterator().hasNext()) {
            postRepository.delete(postsList);
            if (postRepository.getAllByHabitIDOrderByDateOfAdditionDesc(habitID).iterator().hasNext()) {
                throw new RuntimeException("Posts were not deleted properly! CHALLENGE_ID: " + habitID);
            }
        }

        if (tasksHistoryEntities.iterator().hasNext()) {
            taskHistoryRepository.delete(tasksHistoryEntities);
            if (taskHistoryRepository.findAllByHabitID(habitID).iterator().hasNext()) {
                throw new RuntimeException("Tasks History were not deleted properly! CHALLENGE_ID: " + habitID);
            }
        }

        if (tasksList.iterator().hasNext()) {
            taskRepository.delete(tasksList);
            if (taskRepository.getAllByHabitID(habitID).iterator().hasNext()) {
                throw new RuntimeException("Tasks were not deleted properly! CHALLENGE_ID: " + habitID);
            }
        }

        if (membersList.iterator().hasNext()) {
            memberRepository.delete(membersList);
            if (memberRepository.findAllByHabitID(habitID).iterator().hasNext()) {
                throw new RuntimeException("Members were not deleted properly! CHALLENGE_ID: " + habitID);
            }
        }

        habitRepository.delete(habitID);
        if (habitRepository.findById(habitID) != null) {
            throw new RuntimeException("Challenge was not deleted properly! CHALLENGE_ID: " + habitID);
        }

        return HttpStatus.OK;
    }

    @Override
    public Iterable<HabitDTO> getAllHabitsByCategory(Category category) {
        return convertManyToDTOs(habitRepository.findAllByCategory(category), true);
    }

    @Override
    public Iterable<HabitDTO> getAllHabitsBySorting(Sorting sorting) {
        if (sorting.equals(Sorting.Popular)) {
            Iterable<HabitDTO> list = listAllHabits();
            Iterator<HabitDTO> i = list.iterator();
            if (i.hasNext()) {
                List resultList = new ArrayList(0);
                Integer temp;
                HabitDTO tempHabit = null;

                while (i.hasNext()) {
                    temp = 0;
                    for (HabitDTO h : list) {
                        if (h.getMembersCount() > temp) {
                            tempHabit = h;
                            temp = h.getMembersCount();
                        }
                    }
                    resultList.add(tempHabit);
                    i.next();
                    i.remove();
                }
                Iterable<HabitDTO> result = resultList;
                return result;
            }
            return null;
        } else if (sorting.equals(Sorting.Newest)) {
            return convertManyToDTOs(habitRepository.findAllByOrderByHabitStartDateDesc(), true);
        }
        return null;
    }

    @Override
    public HabitDTO createNewHabit(HabitViewModel model) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.getToken()).getBody();

        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setCategory(model.getCategory());
        habitDTO.setFrequency(model.getFrequency());
//        habitDTO.members = model.members;
        habitDTO.setStartDate(model.getStartDate());
        habitDTO.setPrivate(model.getPrivate());
        habitDTO.setTitle(model.getTitle());
        habitDTO.setCreatorID(Integer.parseInt(claims.getSubject()));
        habitDTO.setCanUsersInvite(model.getCanUsersInvite());

        Habit resHabit = new Habit();
        resHabit = registerNewHabit(model, Integer.parseInt(claims.getSubject()));

        if (resHabit.getWinner() != "NONE") {
            habitDTO.setFinished(true);
            habitDTO.setWinner(resHabit.getWinner());
        } else {
            habitDTO.setFinished(false);
            habitDTO.setWinner("No one yet! :)");
        }

        return habitDTO;
    }

    @Override
    public HttpStatus joinHabit(JoinHabitViewModel model) {
        UserDTO tempUser = userService.findById(model.getUserID());
        HabitDTO habit = findById(model.getHabitID());
        UserDTO creator = userService.findById(habit.getCreatorID());

        Member newMember = new Member();
        newMember.setHabitID(model.getHabitID());
        newMember.setUserID(model.getUserID());
        newMember.setImageCode(tempUser.getImageCode());
        newMember.setUserLogin(tempUser.getLogin());
        newMember.setPoints(0);

        String ntfDesc = newMember.getUserLogin() + " joined to your challenge \"" + habit.getTitle() + "\"";
        Notification ntf = new EmailNotificationsSender().createInAppNotification(habit.getCreatorID(), ntfDesc, "http://www.goaleaf.com/challenge/" + model.getHabitID(), false);
        if (creator.getNotifications()) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            try {
                sender.newMemberJoined(creator.getEmailAddress(), creator.getLogin(), tempUser.getLogin(), habit);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        memberService.saveMember(newMember);
        return HttpStatus.OK;
    }

    @Override
    public Boolean changeHabitPrivacy(Integer habitID) {
        Habit habit = habitRepository.findById(habitID);

        habit.setPrivate(!habit.getPrivate());
        habitRepository.save(habit);

        return habitRepository.findById(habitID).getPrivate();
    }

    @Override
    public Category changeHabitCategory(Integer habitID, Category category) {
        Habit habit = habitRepository.findById(habitID);

        habit.setCategory(category);
        habitRepository.save(habit);

        return habitRepository.findById(habitID).getCategory();
    }

    @Override
    public HabitPageDTO getAllByCategoryPaging(Integer pageNr, Integer objectsNr, Category category) {
        Pageable pageable = new PageRequest(pageNr, objectsNr);
        Page<Habit> input = habitRepository.findAllByCategory(category, pageable);

        Iterable<HabitDTO> output = convertManyToDTOs(input.getContent(), false);

        return new HabitPageDTO(output, input.getNumber(), input.hasPrevious(), input.hasNext(), input.getTotalPages());
    }

}
