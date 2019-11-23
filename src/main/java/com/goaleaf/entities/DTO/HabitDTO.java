package com.goaleaf.entities.DTO;

import com.goaleaf.entities.Member;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;

import java.util.Date;

public class HabitDTO {

    public Integer id;

    public String title;

    public Category category;

    public Boolean isPrivate;

//    public Set<Member> members;

    public Frequency frequency;

    public Date startDate;

    public Integer creatorID;

    public Integer pointsToWin;

    public Boolean isFinished;

    public String winner;

    public String creatorLogin;

    public Integer membersCount;
}
