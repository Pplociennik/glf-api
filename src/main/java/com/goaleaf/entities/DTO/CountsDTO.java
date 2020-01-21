package com.goaleaf.entities.DTO;

public class CountsDTO {

    private StatsDTO stats;

    private Integer privateHabits;

    private Integer publicHabits;

    private Integer users;

    public CountsDTO(StatsDTO stats, Integer privateHabits, Integer publicHabits, Integer users) {
        this.stats = stats;
        this.privateHabits = privateHabits;
        this.publicHabits = publicHabits;
        this.users = users;
    }

    public CountsDTO() {
    }

    public StatsDTO getStats() {
        return stats;
    }

    public void setStats(StatsDTO stats) {
        this.stats = stats;
    }

    public Integer getPrivateHabits() {
        return privateHabits;
    }

    public void setPrivateHabits(Integer privateHabits) {
        this.privateHabits = privateHabits;
    }

    public Integer getPublicHabits() {
        return publicHabits;
    }

    public void setPublicHabits(Integer publicHabits) {
        this.publicHabits = publicHabits;
    }

    public Integer getUsers() {
        return users;
    }

    public void setUsers(Integer users) {
        this.users = users;
    }
}
