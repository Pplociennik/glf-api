package com.goaleaf.services;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface HabitService {

    Iterable<HabitDTO> listAllHabits();

//    Iterable<Habit> listAllUsersHabits(Integer userID);

    Habit getHabitById(Integer id);

    Habit saveHabit(Habit habit);

    void removeHabit(Integer id);

    Boolean checkIfExists(Integer id);

    Iterable<Habit> listAllHabitsPaging(Integer pageNr, Integer howManyOnPage);

    Habit registerNewHabit(HabitViewModel model, Integer id) throws WrongTitleException;

    Habit findByTitle(String title);

    HabitDTO findById(Integer id);

    Habit findByOwnerName(String ownerName);

    Iterable<Habit> findHabitsByCreatorID(Integer creatorID);

    Map<Integer, Member> getRank(Integer habitID);

    HabitDTO setPointsToWin(Integer habitID, Integer pointsToWin);

    Iterable<HabitDTO> convertManyToDTOs(Iterable<Habit> habits);
}
