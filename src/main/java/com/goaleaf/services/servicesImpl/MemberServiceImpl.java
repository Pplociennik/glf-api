package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Member;
import com.goaleaf.entities.Notification;
import com.goaleaf.repositories.HabitRepository;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.MemberService;
import com.goaleaf.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private HabitRepository habitRepository;
    @Autowired
    private UserService userService;

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
        return convertManyToDTOs(memberRepository.findAllByHabitID(habitID), true);
    }

    @Override
    public Iterable<MemberDTO> getAll() {
        return convertManyToDTOs(memberRepository.findAll(), false);
    }

    @Override
    public Integer countAllHabitMembers(Integer habitID) {
        Integer result = new Integer(0);
        Iterable<Member> list = memberRepository.findAllByHabitID(habitID);

        for (Member m : list) {
            if (!m.getBanned()) {
                result++;
            }
        }
        return result;
    }

    public void removeSpecifiedMember(Integer habitID, Integer userID) {
        memberRepository.deleteByHabitIDAndUserID(habitID, userID);
    }

    public MemberDTO findSpecifiedMember(Integer habitID, Integer userID) {
        Member member = memberRepository.findByHabitIDAndUserID(habitID, userID);

        if (member == null) {
            return null;
        }
        return convertOneToDTO(memberRepository.findByHabitIDAndUserID(habitID, userID));
    }

    public Map<Integer, MemberDTO> getRank(Integer habitID) {
        Iterable<Member> data = memberRepository.getAllByHabitIDOrderByPointsDesc(habitID);
        Map<Integer, MemberDTO> resultMap = new LinkedHashMap<>();
        Integer i = 1;

        for (Member m : data) {
            if (m.getBanned()) {
                continue;
            }
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

    @Override
    public Boolean checkIfExist(Integer userID, Integer habitID) {
        return memberRepository.existsByHabitIDAndAndUserID(habitID, userID);
    }

    @Override
    public MemberDTO banAMember(Integer userID, Integer habitID) {
        Member member = memberRepository.findByHabitIDAndUserID(habitID, userID);
        UserDTO u = userService.findById(userID);
        Habit habit = habitRepository.findById(habitID);

        if (member.getBanned()) {
            throw new RuntimeException("User already banned!");
        }

        member.setBanned(true);
        Member result = memberRepository.save(member);

        String ntfDesc = "You have been kicked from the challenge \"" + habit.getHabitTitle() + "\"!";
        Notification ntf = new EmailNotificationsSender().createInAppNotification(u.getUserID(), ntfDesc, null, false);
        if (u.getNotifications()) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            try {
                sender.userBanned(u.getEmailAddress(), u.getLogin(), habit);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }

        return convertOneToDTO(result);
    }

    @Override
    public Boolean checkIfUserIsBanned(Integer userID, Integer habitID) {
        Member member = memberRepository.findByHabitIDAndUserID(habitID, userID);

        if (member == null) {
            return false;
        }

        return member.getBanned();
    }

    private MemberDTO convertOneToDTO(Member member) {
        MemberDTO memberDTO = new MemberDTO();

        memberDTO.setHabitID(member.getHabitID());
        memberDTO.setId(member.getId());
        memberDTO.setImageCode(member.getImageCode());
        memberDTO.setPoints(member.getPoints());
        memberDTO.setUserID(member.getUserID());
        memberDTO.setUserLogin(member.getUserLogin());
        memberDTO.setBanned(member.getBanned());

        return memberDTO;
    }

    private Iterable<MemberDTO> convertManyToDTOs(Iterable<Member> input, boolean filterBanned) {
        List<MemberDTO> output = new ArrayList<>(0);

        for (Member m : input) {
            if (filterBanned && m.getBanned()) {
                continue;
            }
            output.add(convertOneToDTO(m));
        }

        return output;
    }


}
