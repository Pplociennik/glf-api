package com.goaleaf.entities.DTO;

import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;

import java.util.Date;

public class HabitDTO {

    private Integer id;

    private String title;

    private Category category;

    private Boolean isPrivate;

//    public Set<Member> members;

    private Frequency frequency;

    private Date startDate;

    private Integer creatorID;

    private Integer pointsToWin;

    private Boolean isFinished;

    private String winner;

    private String creatorLogin;

    private Integer membersCount;

    private Boolean canUsersInvite;

    private Boolean allowDiscussion;

    public HabitDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public Integer getPointsToWin() {
        return pointsToWin;
    }

    public void setPointsToWin(Integer pointsToWin) {
        this.pointsToWin = pointsToWin;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        isFinished = finished;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getCreatorLogin() {
        return creatorLogin;
    }

    public void setCreatorLogin(String creatorLogin) {
        this.creatorLogin = creatorLogin;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }

    public Boolean getCanUsersInvite() {
        return canUsersInvite;
    }

    public void setCanUsersInvite(Boolean canUsersInvite) {
        this.canUsersInvite = canUsersInvite;
    }

    public Boolean getAllowDiscussion() {
        return allowDiscussion;
    }

    public void setAllowDiscussion(Boolean allowDiscussion) {
        this.allowDiscussion = allowDiscussion;
    }
}
