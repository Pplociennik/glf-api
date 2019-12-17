package com.goaleaf.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.Date;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "refId", scope = Stats.class)
@Entity
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Date day;

    private Integer createdAccounts;

    private Integer loggedUsers;

    private Integer createdChallenges;

    private Integer setGoals;

    private Integer createdTasks;

    private Integer completedTasks;

    private Integer createdPosts;

    private Integer commentedPosts;

    private Integer invitedMembers;

    private Integer finishedChallenges;


    public Stats() {
        this.day = new Date();
        this.createdAccounts = 0;
        this.loggedUsers = 0;
        this.createdChallenges = 0;
        this.setGoals = 0;
        this.createdTasks = 0;
        this.completedTasks = 0;
        this.createdPosts = 0;
        this.commentedPosts = 0;
        this.invitedMembers = 0;
        this.finishedChallenges = 0;
    }

    public Integer getId() {
        return id;
    }

    public Date getDay() {
        return day;
    }

    public Integer getCreatedAccounts() {
        return createdAccounts;
    }

    public Integer getLoggedUsers() {
        return loggedUsers;
    }

    public Integer getCreatedChallenges() {
        return createdChallenges;
    }

    public Integer getSetGoals() {
        return setGoals;
    }

    public Integer getCreatedTasks() {
        return createdTasks;
    }

    public Integer getCreatedPosts() {
        return createdPosts;
    }

    public Integer getCommentedPosts() {
        return commentedPosts;
    }

    public Integer getInvitedMembers() {
        return invitedMembers;
    }

    public Integer getFinishedChallenges() {
        return finishedChallenges;
    }

    public Integer getCompletedTasks() {
        return completedTasks;
    }

    public void increaseCreatedAccounts() {
        this.createdAccounts++;
    }

    public void increaseLoggedUsers() {
        this.loggedUsers++;
    }

    public void increaseCreatedChallenges() {
        this.createdChallenges++;
    }

    public void increaseSetGoals() {
        this.setGoals++;
    }

    public void increaseCreatedTasks() {
        this.createdTasks++;
    }

    public void increaseCreatedPosts() {
        this.createdPosts++;
    }

    public void increaseCommentedPosts() {
        this.commentedPosts++;
    }

    public void increaseInvitedMembers() {
        this.invitedMembers++;
    }

    public void increaseFinishedChallenges() {
        this.finishedChallenges++;
    }

    public void increaseCompletedTasks() {
        this.completedTasks++;
    }
}