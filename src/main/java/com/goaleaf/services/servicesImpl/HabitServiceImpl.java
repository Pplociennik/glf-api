package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.*;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.UserDto;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Sorting;
import com.goaleaf.entities.viewModels.habitsCreating.AddMemberViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.repositories.*;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.MemberService;
import com.goaleaf.services.NotificationService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import com.goaleaf.validators.exceptions.habitsProcessing.BadGoalValueException;
import com.goaleaf.validators.exceptions.habitsProcessing.UserAlreadyInHabitException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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


    @Override
    public Iterable<HabitDTO> listAllHabits() {
        Iterable<Habit> input = habitRepository.findAll();
        return convertManyToDTOs(input);
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
    public Iterable<Habit> listAllHabitsPaging(Integer pageNr, Integer howManyOnPage) {
        return habitRepository.findAll(new PageRequest(pageNr, howManyOnPage));
    }

    @Override
    public Habit registerNewHabit(HabitViewModel model, Integer creatorID) throws WrongTitleException {

        Habit newHabit = new Habit();

        newHabit.setHabitStartDate(model.startDate == null ? new Date() : model.startDate);
        newHabit.setFrequency(model.frequency);
        newHabit.setHabitTitle(model.title);
        newHabit.setCategory(model.category);
        newHabit.setPrivate(model.isPrivate);
        newHabit.setCreatorID(creatorID);
        newHabit.setCreatorLogin(userService.findById(creatorID).getLogin());
        newHabit.setWinner("NONE");
        newHabit.setPointsToWIn(1001);
        newHabit.setCanUsersInvite(model.canUsersInvite == null ? true : model.canUsersInvite);
        newHabit.setFinished(false);

        Habit added = new Habit();
        added = habitRepository.save(newHabit);

        Member creator = new Member();
        creator.setUserID(creatorID);
        creator.setHabitID(added.getId());
        creator.setUserLogin(userService.findById(creatorID).getLogin());
        creator.setImageCode(userService.findById(creatorID).getImageName());
        creator.setPoints(0);

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
    public Map<Integer, Member> getRank(Integer habitID) {
        return memberService.getRank(habitID);
    }

    private HabitDTO convertToDTO(Habit entry) {

        if (entry == null) {
            return null;
        }

        UserDto creator = userService.findById(entry.getCreatorID());

        HabitDTO habitDTO = new HabitDTO();
        habitDTO.id = entry.getId();
        habitDTO.category = entry.getCategory();
        habitDTO.frequency = entry.getFrequency();
//        habitDTO.members = model.members;
        habitDTO.startDate = entry.getHabitStartDate();
        habitDTO.isPrivate = entry.getPrivate();
        habitDTO.title = entry.getHabitTitle();
        habitDTO.creatorID = entry.getCreatorID();
        habitDTO.creatorLogin = creator.getLogin();
        habitDTO.membersCount = memberService.countAllHabitMembers(entry.getId());
        habitDTO.canUsersInvite = entry.getCanUsersInvite();

        if (entry.getPointsToWIn() != 1001) {
            habitDTO.pointsToWin = entry.getPointsToWIn();
        } else {
            habitDTO.pointsToWin = 0;
        }

        if (!entry.getWinner().equals("NONE")) {
            habitDTO.isFinished = true;
            habitDTO.winner = entry.getWinner();
        } else {
            habitDTO.isFinished = false;
            habitDTO.winner = "NONE";
        }

        return habitDTO;
    }

    public Iterable<HabitDTO> convertManyToDTOs(Iterable<Habit> habits) {
        List<HabitDTO> resultList = new ArrayList<>(0);

        for (Habit h : habits) {
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
                .parseClaimsJws(model.token).getBody();

        UserDto inviter = userService.findById(Integer.parseInt(claims.getSubject()));
        Habit habit = habitRepository.findById(model.habitID);

        if (!habit.getCanUsersInvite()) {
            if (!habit.getCreatorLogin().equals(inviter.getLogin())) {
                throw new RuntimeException("You are not allowed to invite members!");
            }
        }

        UserDto searchingUser = userService.findByLogin(model.userLogin);

        Member newMember = new Member();
        newMember.setUserID(searchingUser.getUserID());
        newMember.setHabitID(model.habitID);
        newMember.setImageCode(searchingUser.getImageName());
        newMember.setUserLogin(searchingUser.getLogin());
        newMember.setPoints(0);

        if (memberService.checkIfExist(newMember)) {
            throw new UserAlreadyInHabitException("User already participating!");
        }

//        memberService.saveMember(newMember);

        Notification ntf = new Notification();
        ntf.setDate(new Date());
        ntf.setRecipientID(searchingUser.getUserID());
        ntf.setDescription(userService.findById(Integer.parseInt(claims.getSubject())).getLogin() + " invited you to group " + findById(model.habitID).title + "!");
        ntf.setUrl((model.url.isEmpty() ? "EMPTY_URL" : model.url));
        if (notificationService.findByDescription(ntf.getDescription()) == null) {
            notificationService.saveNotification(ntf);
        }

        if (searchingUser.getNotifications()) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            try {
                sender.sendInvitationNotification(searchingUser.getEmailAddress(), searchingUser.getLogin(), userService.findById(Integer.parseInt(claims.getSubject())).getLogin(), findById(model.habitID).title);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

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
        return convertManyToDTOs(habitRepository.findAllByCategory(category));
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
                        if (h.membersCount > temp) {
                            tempHabit = h;
                            temp = h.membersCount;
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
            return convertManyToDTOs(habitRepository.findAllByOrderByHabitStartDateDesc());
        }
        return null;
    }

}
