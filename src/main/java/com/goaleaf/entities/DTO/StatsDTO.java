package com.goaleaf.entities.DTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StatsDTO {

    private String day;

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

    public String getDay() {
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

    public Integer getCompletedTasks() {
        return completedTasks;
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

    public StatsDTO(StatsBuilder builder) {
        this.day = builder.day;
        this.createdAccounts = builder.createdAccounts;
        this.loggedUsers = builder.loggedUsers;
        this.createdChallenges = builder.createdChallenges;
        this.setGoals = builder.setGoals;
        this.createdTasks = builder.createdTasks;
        this.completedTasks = builder.completedTasks;
        this.createdPosts = builder.createdPosts;
        this.commentedPosts = builder.commentedPosts;
        this.invitedMembers = builder.invitedMembers;
        this.finishedChallenges = builder.finishedChallenges;
    }

    public static class StatsBuilder {
        private String day;
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

        public StatsBuilder setDay(Date day) {
            DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
            this.day = df.format(day);
            return this;
        }

        public StatsBuilder setCreatedAccounts(Integer createdAccounts) {
            this.createdAccounts = createdAccounts;
            return this;
        }

        public StatsBuilder setLoggedUsers(Integer loggedUsers) {
            this.loggedUsers = loggedUsers;
            return this;
        }

        public StatsBuilder setCreatedChallenges(Integer createdChallenges) {
            this.createdChallenges = createdChallenges;
            return this;
        }

        public StatsBuilder setSetGoals(Integer setGoals) {
            this.setGoals = setGoals;
            return this;
        }

        public StatsBuilder setCreatedTasks(Integer createdTasks) {
            this.createdTasks = createdTasks;
            return this;
        }

        public StatsBuilder setCompletedTasks(Integer completedTasks) {
            this.completedTasks = completedTasks;
            return this;
        }

        public StatsBuilder setCreatedPosts(Integer createdPosts) {
            this.createdPosts = createdPosts;
            return this;
        }

        public StatsBuilder setCommentedPosts(Integer commentedPosts) {
            this.commentedPosts = commentedPosts;
            return this;
        }

        public StatsBuilder setInvitedMembers(Integer invitedMembers) {
            this.invitedMembers = invitedMembers;
            return this;
        }

        public StatsBuilder setFinishedChallenges(Integer finishedChallenges) {
            this.finishedChallenges = finishedChallenges;
            return this;
        }

        public StatsDTO gainStats() {
            return new StatsDTO(this);
        }
    }
}
