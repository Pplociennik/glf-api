package com.goaleaf.entities.viewModels.habitsCreating;

import com.goaleaf.entities.Member;
import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;

import java.util.Date;
import java.util.Set;

public class HabitViewModel {

    public String title;

    public Category category;

    public Boolean isPrivate;

//    public Set<Member> members;

    public Frequency frequency;

    public Date startDate;

    public String token;

}
