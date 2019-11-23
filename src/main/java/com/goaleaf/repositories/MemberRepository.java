package com.goaleaf.repositories;

import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface MemberRepository extends CrudRepository<Member, Integer> {

    Member findByUserID(Integer id);

    Iterable<Member> findAllByHabitID(Integer habitID);

    Iterable<Member> findAllByUserID(Integer userID);

    Boolean existsByHabitIDAndAndUserID(Integer habitID, Integer userID);

    Integer countAllByHabitID(Integer habitID);

    void deleteByHabitIDAndUserID(Integer habitID, Integer userID);

    Member findByHabitIDAndUserID(Integer habitID, Integer userID);

    Iterable<Member> getAllByHabitIDOrderByPointsDesc(Integer habitID);

    Member getFirstByHabitIDOrderByPointsDesc(Integer habitID);

    Member getByUserID(Integer userID);

}
