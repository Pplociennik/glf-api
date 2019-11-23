package com.goaleaf.controllers;

import com.goaleaf.entities.Member;
import com.goaleaf.services.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<Member> getWholeMembersTable() {
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

}
