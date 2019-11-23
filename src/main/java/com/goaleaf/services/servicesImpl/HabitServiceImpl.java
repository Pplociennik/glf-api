package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.User;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.repositories.HabitRepository;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.MemberService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import com.goaleaf.validators.exceptions.habitsProcessing.BadGoalValueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class HabitServiceImpl implements HabitService {

    @Autowired
    private HabitRepository habitRepository;
    @Autowired
    private MemberService memberService;
    @Autowired
    private UserService userService;


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
        newHabit.setFinished(false);
        newHabit.setWinner("NONE");
        newHabit.setPointsToWIn(1001);
        newHabit.setFinished(false);

        Habit added = new Habit();
        added = habitRepository.save(newHabit);

        Member creator = new Member();
        creator.setUserID(creatorID);
        creator.setHabitID(added.getId());
        creator.setUserLogin(userService.getUserById(creatorID).getLogin());
        creator.setImgName(userService.getUserById(creatorID).getImageName());
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

        User creator = userService.findById(entry.getCreatorID());

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

        if (entry.getPointsToWIn() != null) {
            habitDTO.pointsToWin = entry.getPointsToWIn();
        } else {
            habitDTO.pointsToWin = 0;
        }

        if (entry.getWinner() != "NONE") {
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
}
