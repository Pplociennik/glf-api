package com.goaleaf.services;

import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.DTO.pagination.HabitPageDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Sorting;
import com.goaleaf.entities.viewModels.habitsCreating.AddMemberViewModel;
import com.goaleaf.entities.viewModels.habitsCreating.HabitViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.JoinHabitViewModel;
import com.goaleaf.validators.exceptions.habitsCreating.WrongTitleException;
import org.springframework.http.HttpStatus;
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

    HabitPageDTO listAllHabitsPaging(Integer pageNr, Integer howManyOnPage);

    Habit registerNewHabit(HabitViewModel model, Integer id) throws WrongTitleException;

    Habit findByTitle(String title);

    HabitDTO findById(Integer id);

    Habit findByOwnerName(String ownerName);

    Iterable<Habit> findHabitsByCreatorID(Integer creatorID);

    Map<Integer, MemberDTO> getRank(Integer habitID);

    HabitDTO setPointsToWin(Integer habitID, Integer pointsToWin);

    Iterable<HabitDTO> convertManyToDTOs(Iterable<Habit> habits, boolean filterPrivacy);

    Boolean setInvitingPermissions(Boolean allowed, Integer habitID);

    HttpStatus inviteNewMember(AddMemberViewModel model);

    HttpStatus deleteHabit(Integer habitID, String token);

    Iterable<HabitDTO> getAllHabitsByCategory(Category category);

    Iterable<HabitDTO> getAllHabitsBySorting(Sorting sorting);

    HabitDTO createNewHabit(HabitViewModel model);

    HttpStatus joinHabit(JoinHabitViewModel model);

    Boolean changeHabitPrivacy(Integer habitID);

    Category changeHabitCategory(Integer habitID, Category category);

    HabitPageDTO getAllByCategoryPaging(Integer pageNr, Integer objectsNr, Category category);

    HabitDTO convertToDTO(Habit entry);
}
