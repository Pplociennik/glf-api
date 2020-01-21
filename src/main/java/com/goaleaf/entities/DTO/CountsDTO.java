package com.goaleaf.entities.DTO;

public class CountsDTO {

    private StatsDTO stats;

    private Integer privateHabits;

    private Integer publicHabits;

    public CountsDTO(StatsDTO stats, Integer privateHabits, Integer publicHabits) {
        this.stats = stats;
        this.privateHabits = privateHabits;
        this.publicHabits = publicHabits;
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
}
