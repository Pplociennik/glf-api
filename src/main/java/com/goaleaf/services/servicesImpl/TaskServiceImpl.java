package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.CompleteTaskDTO;
import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.DTO.pagination.TaskPageDTO;
import com.goaleaf.entities.*;
import com.goaleaf.entities.enums.Frequency;
import com.goaleaf.entities.enums.PostTypes;
import com.goaleaf.entities.viewModels.NewTaskViewModel;
import com.goaleaf.repositories.*;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.JwtService;
import com.goaleaf.services.StatsService;
import com.goaleaf.services.TaskService;
import com.goaleaf.validators.exceptions.habitsProcessing.PointsNotSetException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javassist.NotFoundException;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.goaleaf.security.SecurityConstants.SECRET;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private StatsService statsService;


    @Override
    public Iterable<TaskDTO> getAllTasks() {
        return convertToViewModel(taskRepository.findAll());
    }

    @Override
    public Iterable<TaskDTO> getAllByCreatorID(Integer creatorID) {
        if (taskRepository.getAllByCreatorID(creatorID) == null) {
            try {
                throw new NotFoundException("The user have created no task");
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return convertToViewModel(taskRepository.getAllByCreatorID(creatorID));
    }

    @Override
    public Iterable<TaskDTO> getAllByHabitID(Integer habitID) {
        if (taskRepository.getAllByHabitID(habitID) == null) {
            try {
                throw new NotFoundException("No such tasks!");
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return convertToViewModel(taskRepository.getAllByHabitID(habitID));
    }

    @Override
    public Iterable<TaskDTO> getAllByCreatorIDAndHabitID(Integer creatorID, Integer habitID) {
        if (taskRepository.getAllByCreatorIDAndHabitID(creatorID, habitID) == null) {
            try {
                throw new NotFoundException("No such tasks!");
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
        }
        return convertToViewModel(taskRepository.getAllByCreatorIDAndHabitID(creatorID, habitID));
    }

    @Override
    public Iterable<TaskDTO> getAvailableTasks(Integer habitID, Integer userID) {
        Iterable<Task> input = taskRepository.getAllByHabitID(habitID);
        List<TaskDTO> output = new ArrayList<>(0);

        for (Task t : input) {
            if (!t.getCompleted()) {
                TaskDTO model = convertToViewModel(t, userID);
                if (model != null) {
                    output.add(model);
                }
            }
        }
        Iterable<TaskDTO> result = output;
        return result;
    }

    @Override
    public TaskPageDTO getAvailableTasksPaging(Integer pageNr, Integer objectsNr, Integer habitID, Integer userID) {
        Pageable pageable = new PageRequest(pageNr, objectsNr);

        List<TaskDTO> list = (List<TaskDTO>) getAvailableTasks(habitID, userID);

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<TaskDTO> pages = new PageImpl<TaskDTO>(list.subList(start, end), pageable, list.size());

        return new TaskPageDTO(pages.getContent(), pages.getNumber(), pages.hasPrevious(), pages.hasNext(), pages.getTotalPages());
    }

    @Override
    public TaskPageDTO getAllHabitTasksPaging(Integer pageNr, Integer objectsNr, Integer habitID) {
        Pageable pageable = new PageRequest(pageNr, objectsNr);
        Page<Task> list = taskRepository.findAllByHabitIDOrderByCreationDateDesc(habitID, pageable);

        Iterable<Task> input = list.getContent();
        Iterable<TaskDTO> output = convertToViewModel(input);

        return new TaskPageDTO(output, list.getNumber(), list.hasPrevious(), list.hasNext(), list.getTotalPages());
    }

    @Override
    public Integer countUserTasks(Integer userID) {
        return taskRepository.countAllByCreatorID(userID);
    }

    @Override
    public Integer countHabitTasks(Integer habitID) {
        return taskRepository.countAllByHabitID(habitID);
    }

    @Override
    public TaskDTO saveTask(NewTaskViewModel newTask) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(newTask.getToken()).getBody();

        Task newT = new Task(Integer.parseInt(claims.getSubject()), newTask.getHabitID(), newTask.getDescription(), newTask.getPoints(), false, newTask.getFrequency(), Integer.parseInt(claims.getSubject()), newTask.getDaysInterval());

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseCreatedTasks();
        statsService.save(stats);

        Task returned = taskRepository.save(newT);
        Habit habit = habitRepository.findById(newTask.getHabitID());

        Iterable<Member> members = memberRepository.findAllByHabitID(returned.getHabitID());

        String ntfDesc = "New task is available in habit \"" + habit.getHabitTitle() + "\"";
        EmailNotificationsSender sender = new EmailNotificationsSender();
        for (Member m : members) {
            User user = userRepository.findById(m.getUserID());
            if (user.getNotifications() && user.getId() != returned.getCreatorID()) {
                try {
                    Notification ntf = new EmailNotificationsSender().createInAppNotification(m.getUserID(), ntfDesc, "http://www.goaleaf.com/habit/" + newTask.getHabitID(), false);
                    sender.taskCreated(user.getEmailAddress(), user.getLogin(), habit);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }


        return convertToViewModel(returned, null);

    }

    @Override
    public Post completeTask(CompleteTaskDTO cmp) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(cmp.getToken()).getBody();

        User user = userRepository.findById(Integer.parseInt(claims.getSubject()));
        Task task = taskRepository.getById(cmp.getTaskID());
        Member member = memberRepository.findByHabitIDAndUserID(cmp.getHabitID(), user.getId());
        Habit habit = habitRepository.findById(cmp.getHabitID());

        if (habit.getPointsToWIn() == null || habit.getPointsToWIn() == 0) {
            throw new PointsNotSetException("Points to win have never been set!");
        }

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseCompletedTasks();
        statsService.save(stats);

        member.addPoints(task.getPoints());
        memberRepository.save(member);

        TasksHistoryEntity ent = createNewHistoryEntity(user, habit, task);

        if (member.getPoints() >= habit.getPointsToWIn()) {
            habit.setWinner(user.getLogin());
            habit.setFinished(true);

            Post post = setTaskAsCompleted(task, user, cmp, PostTypes.Task, habit, member);

            Post result = setTaskAsCompleted(task, user, cmp, PostTypes.HabitFinished, habit, member);

            Stats statss = statsService.findStatsByDate(new Date());
            if (statss == null) {
                statss = new Stats();
            }
            statss.increaseFinishedChallenges();
            statsService.save(statss);

            String ntfDesc = "Challenge: " + habit.getHabitTitle() + " has ended!";
            Iterable<Member> members = memberRepository.findAllByHabitID(habit.getId());
            for (Member m : members) {
                User u = userRepository.findById(m.getUserID());
                Notification ntf = new EmailNotificationsSender().createInAppNotification(m.getUserID(), ntfDesc, "http://www.goaleaf.com/habit/" + habit.getId(), false);
                if (u.getNotifications()) {
                    EmailNotificationsSender sender = new EmailNotificationsSender();
                    try {
                        sender.challengeEnded(u.getEmailAddress(), u.getLogin(), user.getLogin(), habit);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            }

            //PostDTO dto = new PostDTO(aS.getCreatorLogin(), aS.getPostText(), aS.getPostType(), aS.getDateOfAddition());
            return result;
        }

        Post result = setTaskAsCompleted(task, user, cmp, PostTypes.Task, habit, member);
        result.setCreatorImage(user.getImageCode());

        //PostDTO dto = new PostDTO(aS.getCreatorLogin(), aS.getPostText(), aS.getPostType(), aS.getDateOfAddition());
        return result;
    }

    private Post setTaskAsCompleted(Task task, User user, CompleteTaskDTO cmp, PostTypes type, Habit habit, Member member) {

        if (task.getFrequency().equals(Frequency.Daily) || task.getFrequency().equals(Frequency.Once4All)) {
            task.setCompleted(false);
        } else {
            task.setCompleted(true);
        }
        task.setExecutor(user.getLogin());
        task.setExecutorID(user.getId());
        task.setLastDone(new Date());
        taskRepository.save(task);

        Post newPost = new Post();
        newPost.setPostType(type);
        newPost.setCreatorLogin(user.getLogin());
        newPost.setDateOfAddition(new Date());
        newPost.setHabitID(cmp.getHabitID());
        newPost.setPostText((type.equals(PostTypes.Task) ? task.getDescription() : "User " + user.getLogin() + " has won the challenge \"" + habit.getHabitTitle() + "\" gaining " + member.getPoints() + " points! Congratulations!"));
        newPost.setUserComment(cmp.getComment());
        newPost.setUserComment(cmp.getComment());
        newPost.setTaskPoints(task.getPoints());
        newPost.setTaskID(task.getId());
        newPost.setCreatorImage(user.getImageCode());
        return postRepository.save(newPost);
    }

    private TasksHistoryEntity createNewHistoryEntity(User user, Habit habit, Task task) {
        TasksHistoryEntity ent = new TasksHistoryEntity();
        ent.setExecutionDate(new Date());
        ent.setHabitID(habit.getId());
        ent.setPoints(task.getPoints());
        ent.setTaskDescription(task.getDescription());
        ent.setTaskID(task.getId());
        ent.setUserID(user.getId());
        ent.setUserName(user.getLogin());
        return taskHistoryRepository.save(ent);
    }

    @Override
    public TaskDTO getTaskByID(Integer taskID) {
        return convertToViewModel(taskRepository.getById(taskID), null);
    }

    Iterable<TaskDTO> convertToViewModel(Iterable<Task> input) {
        List resultList = new ArrayList<TaskDTO>(0);
        for (Task t : input) {
            TaskDTO model = convertToViewModel(t, null);
            //TaskDTO model = new TaskDTO(t.getId(), u.getLogin(), t.getDescription(), t.getPoints(), t.getFrequency(), t.getDaysInterval(), refreshDate, active, t.getExecutor());
            resultList.add(model);
        }
        Iterable<TaskDTO> outputList = resultList;
        return outputList;
    }

    public TaskDTO convertToViewModel(Task task, Integer id) {

        User u = userRepository.findById(task.getCreatorID());
        TasksHistoryEntity tempHistoryEntity = null;
        Iterable<TasksHistoryEntity> historyList = taskHistoryRepository.findAllByTaskIDAndUserID(task.getId(), id);
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        Date currentDate = new Date();

        if (task.getFrequency().equals(Frequency.Once4All)) {
            if (!historyList.iterator().hasNext()) {
                return new TaskDTO(task.getId(), u.getLogin(), task.getDescription(), task.getPoints(), task.getFrequency(), null, null, true, null);
            } else {
                return null;
            }
        }

        for (TasksHistoryEntity h : historyList) {
            if (dateTimeComparator.compare(currentDate, h.getExecutionDate()) == 0) {
                tempHistoryEntity = h;
            }
        }

        Calendar c = Calendar.getInstance();

        c.setTime(task.getLastDone());
        c.add(Calendar.DAY_OF_MONTH, task.getDaysInterval());
        Date refreshDate = tempHistoryEntity != null ? c.getTime() : new Date();

        Boolean active = false;
        active = (tempHistoryEntity == null ? true : false);

        if (task.getFrequency().equals(Frequency.Daily)) {
            if (dateTimeComparator.compare(currentDate, refreshDate) < 0) {
                active = false;
            }
            else {
                active = true;
            }
        }

        return new TaskDTO(task.getId(), u.getLogin(), task.getDescription(), task.getPoints(), task.getFrequency(), task.getDaysInterval(), refreshDate, active, task.getExecutor());
    }

    @Override
    public HttpStatus pushBachTaskCompletion(Integer taskID) {
        Task task = taskRepository.getById(taskID);
        Member member = memberRepository.getByUserID(task.getExecutorID());

        member.decreasePoints(task.getPoints());

        memberRepository.save(member);

        taskRepository.delete(taskID);

        if (taskRepository.getById(taskID) == null) {
            return HttpStatus.OK;
        }
        return HttpStatus.EXPECTATION_FAILED;
    }

    public HttpStatus justRemoveTaskFromDatabase(Integer taskID) {
        taskRepository.delete(taskID);

        if (taskRepository.getById(taskID) == null) {
            return HttpStatus.OK;
        }

        return HttpStatus.EXPECTATION_FAILED;
    }
}
