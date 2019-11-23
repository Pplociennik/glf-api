package com.goaleaf.services;

import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public interface MemberService {

    Member getByUserID(Integer id);

    Member saveMember(Member member);

    Boolean checkIfExist(Member member);

    Iterable<Member> getAllByHabitID(Integer habitID);

    Iterable<Member> getAll();

    Integer countAllHabitMembers(Integer habitID);

    void removeSpecifiedMember(Integer habitID, Integer userID);

    Member findSpecifiedMember(Integer habitID, Integer userID);

    Map<Integer, Member> getRank(Integer habitID);

    Integer getUserPoints(Integer habitID, Integer userID);

    Integer getLeaderPoints(Integer habitID);

}
