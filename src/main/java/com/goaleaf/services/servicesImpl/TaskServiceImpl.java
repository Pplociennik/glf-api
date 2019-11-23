package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.CompleteTaskDTO;
import com.goaleaf.entities.DTO.TaskDTO;
import com.goaleaf.entities.*;
import com.goaleaf.entities.enums.Frequency;
import com.goaleaf.entities.enums.PostTypes;
import com.goaleaf.entities.viewModels.TaskViewModel;
import com.goaleaf.repositories.*;
import com.goaleaf.security.SecurityConstants;
import com.goaleaf.services.JwtService;
import com.goaleaf.services.TaskService;
import com.goaleaf.validators.exceptions.habitsProcessing.PointsNotSetException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javassist.NotFoundException;
import org.joda.time.DateTimeComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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


    @Override
    public Iterable<TaskViewModel> getAllTasks() {
        return convertToViewModel(taskRepository.findAll());
    }

    @Override
    public Iterable<TaskViewModel> getAllByCreatorID(Integer creatorID) {
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
    public Iterable<TaskViewModel> getAllByHabitID(Integer habitID) {
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
    public Iterable<TaskViewModel> getAllByCreatorIDAndHabitID(Integer creatorID, Integer habitID) {
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
    public Iterable<TaskViewModel> getAvailableTasks(Integer habitID, Integer userID) {
        Iterable<Task> input = taskRepository.getAllByHabitID(habitID);
        List<TaskViewModel> output = new ArrayList<>(0);

        for (Task t : input) {
            if (!t.getCompleted()) {
                output.add(convertToViewModel(t, userID));
            }
        }
        Iterable<TaskViewModel> result = output;
        return result;
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
    public TaskViewModel saveTask(TaskDTO newTask) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(newTask.getToken()).getBody();

        Task newT = new Task(Integer.parseInt(claims.getSubject()), newTask.getHabitID(), newTask.getDescription(), newTask.getPoints(), false, newTask.getFrequency(), Integer.parseInt(claims.getSubject()), newTask.getDaysInterval());

        Task returned = taskRepository.save(newT);

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

        member.addPoints(task.getPoints());
        memberRepository.save(member);

        TasksHistoryEntity ent = createNewHistoryEntity(user, habit, task);

        if (member.getPoints() >= habit.getPointsToWIn()) {
            habit.setWinner(user.getLogin());
            habit.setFinished(true);

            Post post = setTaskAsCompleted(task, user, cmp, PostTypes.Task, habit, member);

            Post result = setTaskAsCompleted(task, user, cmp, PostTypes.HabitFinished, habit, member);

            //PostDTO dto = new PostDTO(aS.getCreatorLogin(), aS.getPostText(), aS.getPostType(), aS.getDateOfAddition());
            return result;
        }

        Post result = setTaskAsCompleted(task, user, cmp, PostTypes.Task, habit, member);

        //PostDTO dto = new PostDTO(aS.getCreatorLogin(), aS.getPostText(), aS.getPostType(), aS.getDateOfAddition());
        return result;
    }

    private Post setTaskAsCompleted(Task task, User user, CompleteTaskDTO cmp, PostTypes type, Habit habit, Member member) {

        if (task.getFrequency().equals(Frequency.Daily)) {
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
        newPost.setPostText((type.equals(PostTypes.Task) ? task.getDescription() : "User " + user.getLogin() + " has won the competition " + habit.getHabitTitle() + " gaining " + member.getPoints() + " points! Congratulations!"));
        newPost.setUserComment(cmp.getComment());
        newPost.setUserComment(cmp.getComment());
        newPost.setTaskPoints(task.getPoints());
        newPost.setTaskID(task.getId());
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
    public TaskViewModel getTaskByID(Integer taskID) {
        return convertToViewModel(taskRepository.getById(taskID), null);
    }

    Iterable<TaskViewModel> convertToViewModel(Iterable<Task> input) {
        List resultList = new ArrayList<TaskViewModel>(0);
        for (Task t : input) {
            TaskViewModel model = convertToViewModel(t, null);
            //TaskViewModel model = new TaskViewModel(t.getId(), u.getLogin(), t.getDescription(), t.getPoints(), t.getFrequency(), t.getDaysInterval(), refreshDate, active, t.getExecutor());
            resultList.add(model);
        }
        Iterable<TaskViewModel> outputList = resultList;
        return outputList;
    }

    public TaskViewModel convertToViewModel(Task task, Integer id) {

        User u = userRepository.findById(task.getCreatorID());
        TasksHistoryEntity tempHistoryEntity = null;
        Iterable<TasksHistoryEntity> historyList = taskHistoryRepository.findAllByTaskIDAndUserID(task.getId(), id);
        DateTimeComparator dateTimeComparator = DateTimeComparator.getDateOnlyInstance();
        Date currentDate = new Date();

        for (TasksHistoryEntity h : historyList) {
            if (dateTimeComparator.compare(currentDate, h.getExecutionDate()) == 0) {
                tempHistoryEntity = h;
            }
        }

        Calendar c = Calendar.getInstance();

        c.setTime(task.getLastDone());
        c.add(Calendar.DAY_OF_MONTH, task.getDaysInterval());
        Date refreshDate = c.getTime();

        Boolean active = false;
        active = (tempHistoryEntity == null ? true : false);

        return new TaskViewModel(task.getId(), u.getLogin(), task.getDescription(), task.getPoints(), task.getFrequency(), task.getDaysInterval(), refreshDate, active, task.getExecutor());
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
