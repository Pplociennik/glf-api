package com.goaleaf.repositories;

import com.goaleaf.entities.Habit;
import com.goaleaf.entities.enums.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface HabitRepository extends CrudRepository<Habit, Integer>, PagingAndSortingRepository<Habit, Integer> {

    Habit findById(Integer Id);

    @Query("select count(*) from Habit p where p.id = ?1")
    Integer checkIfExists(Integer id);

    Habit findByHabitTitle(String title);

    Iterable<Habit> findAllByCreatorID(Integer creatorID);

    @Override
    void delete(Integer integer);

    Iterable<Habit> findAllByCategory(Category category);

    Iterable<Habit> findAllByCategoryOrderByHabitStartDateDesc(Category category);

    Iterable<Habit> findAllByOrderByHabitStartDateDesc();

    Page<Habit> findAllByCategory(Category category, Pageable pageable);

    Page<Habit> findAllByFinished(Boolean finished, Pageable pageable);

    Page<Habit> findAll(Pageable pageable);

    Iterable<Habit> findAllByFinished(Boolean finished);

    Page<Habit> findAllByOrderByHabitStartDateDesc(Pageable pageable);

    Habit findByHabitStartDate(Date date);

    //    Habit findByUserName(String userName);

//    Habit findByLogin(String login);
}
