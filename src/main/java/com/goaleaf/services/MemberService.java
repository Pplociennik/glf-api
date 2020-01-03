package com.goaleaf.services;

import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.Member;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MemberService {

    MemberDTO getByUserID(Integer id);

    MemberDTO saveMember(Member member);

    Boolean checkIfExist(Member member);

    Iterable<MemberDTO> getAllByHabitID(Integer habitID);

    Iterable<MemberDTO> getAll();

    Integer countAllHabitMembers(Integer habitID);

    void removeSpecifiedMember(Integer habitID, Integer userID);

    MemberDTO findSpecifiedMember(Integer habitID, Integer userID);

    Map<Integer, MemberDTO> getRank(Integer habitID);

    Integer getUserPoints(Integer habitID, Integer userID);

    Integer getLeaderPoints(Integer habitID);

    Boolean checkIfExist(Integer userID, Integer habitID);

    MemberDTO banAMember(Integer userID, Integer habitID);

    Boolean checkIfUserIsBanned(Integer userID, Integer habitID);

}
