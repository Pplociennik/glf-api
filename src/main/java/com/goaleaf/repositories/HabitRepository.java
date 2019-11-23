package com.goaleaf.repositories;

import com.goaleaf.entities.Habit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends CrudRepository<Habit, Integer>, PagingAndSortingRepository<Habit, Integer> {

    Habit findById(Integer Id);

    @Query("select count(*) from Habit p where p.id = ?1")
    Integer checkIfExists(Integer id);

    Habit findByHabitTitle(String title);

    Iterable<Habit> findAllByCreatorID(Integer creatorID);

//    Habit findByUserName(String userName);

//    Habit findByLogin(String login);
}
