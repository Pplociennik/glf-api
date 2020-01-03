package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.Member;
import com.goaleaf.repositories.HabitRepository;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private HabitRepository habitRepository;

    @Override
    public MemberDTO getByUserID(Integer id) {
        return convertOneToDTO(memberRepository.findByUserID(id));
    }

    @Override
    public MemberDTO saveMember(Member member) {
        return convertOneToDTO(memberRepository.save(member));
    }

    public Boolean checkIfExist(Member member) {
        return memberRepository.existsByHabitIDAndAndUserID(member.getHabitID(), member.getUserID());
    }

    @Override
    public Iterable<MemberDTO> getAllByHabitID(Integer habitID) {
        return convertManyToDTOs(memberRepository.findAllByHabitID(habitID));
    }

    @Override
    public Iterable<MemberDTO> getAll() {
        return convertManyToDTOs(memberRepository.findAll());
    }

    @Override
    public Integer countAllHabitMembers(Integer habitID) {
        return memberRepository.countAllByHabitID(habitID);
    }

    public void removeSpecifiedMember(Integer habitID, Integer userID) {
        memberRepository.deleteByHabitIDAndUserID(habitID, userID);
    }

    public MemberDTO findSpecifiedMember(Integer habitID, Integer userID) {
        return convertOneToDTO(memberRepository.findByHabitIDAndUserID(habitID, userID));
    }

    public Map<Integer, MemberDTO> getRank(Integer habitID) {
        Iterable<Member> data = memberRepository.getAllByHabitIDOrderByPointsDesc(habitID);
        Map<Integer, MemberDTO> resultMap = new LinkedHashMap<>();
        Integer i = 1;

        for (Member m : data) {
            resultMap.put(i, convertOneToDTO(m));
            i++;
        }

        return resultMap;
    }

    public Integer getUserPoints(Integer habitID, Integer userID) {
        Member member = memberRepository.findByHabitIDAndUserID(habitID, userID);

        return member.getPoints();
    }

    @Override
    public Integer getLeaderPoints(Integer habitID) {
        Member member = memberRepository.getFirstByHabitIDOrderByPointsDesc(habitID);
        return member.getPoints();
    }

    private MemberDTO convertOneToDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setHabitID(member.getHabitID());
        memberDTO.setId(member.getId());
        memberDTO.setImageCode(member.getImageCode());
        memberDTO.setPoints(member.getPoints());
        memberDTO.setUserID(member.getUserID());
        memberDTO.setUserLogin(member.getUserLogin());

        return memberDTO;
    }

    private Iterable<MemberDTO> convertManyToDTOs(Iterable<Member> input) {
        List<MemberDTO> output = new ArrayList<>(0);

        for (Member m : input) {
            output.add(convertOneToDTO(m));
        }

        return output;
    }


}
