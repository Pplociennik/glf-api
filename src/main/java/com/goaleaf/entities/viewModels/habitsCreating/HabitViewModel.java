package com.goaleaf.entities.viewModels.habitsCreating;

import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;

import java.util.Date;

public class HabitViewModel {

    private String title;

    private Category category;

    private Boolean isPrivate;

//    public Set<Member> members;

    private Frequency frequency;

    private Date startDate;

    private String token;

    private Boolean canUsersInvite;

    public HabitViewModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getCanUsersInvite() {
        return canUsersInvite;
    }

    public void setCanUsersInvite(Boolean canUsersInvite) {
        this.canUsersInvite = canUsersInvite;
    }
}
