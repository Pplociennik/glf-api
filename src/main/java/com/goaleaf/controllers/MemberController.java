package com.goaleaf.controllers;

import com.goaleaf.entities.DTO.MemberDTO;
import com.goaleaf.entities.DTO.pagination.MemberPageDTO;
import com.goaleaf.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<MemberDTO> getWholeMembersTable() {
        return memberService.getAll();
    }

    @RequestMapping(value = "/member/points", method = RequestMethod.GET)
    public Integer getPoints(@RequestParam Integer habitsID, Integer userID) {
        return memberService.getUserPoints(habitsID, userID);
    }

    @RequestMapping(value = "/leader/points", method = RequestMethod.GET)
    public Integer getLeaderPointsResult(@RequestParam Integer habitID) {
        return memberService.getLeaderPoints(habitID);
    }

    @GetMapping(value = "/habit/paging")
    public MemberPageDTO getHabitMembersPaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Integer habitID) {
        return memberService.getAllHabitMembersPaging(pageNr, objectsNr, habitID);
    }

}
