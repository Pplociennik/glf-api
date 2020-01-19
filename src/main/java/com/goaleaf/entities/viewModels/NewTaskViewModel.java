package com.goaleaf.entities.viewModels;

import com.goaleaf.entities.enums.Frequency;

import java.util.Date;

public class NewTaskViewModel {

    private String token;

    private Integer habitID;

    private String description;

    private Integer points;

    private Frequency frequency;

    private Date lastDone;

    private Integer daysInterval;

    public NewTaskViewModel() {
    }

    public NewTaskViewModel(String token, Integer habitID, String description, Integer points, Frequency frequency, Date lastDone, Integer daysInterval) {
        this.token = token;
        this.habitID = habitID;
        this.description = description;
        this.points = points;
        this.frequency = frequency;
        this.lastDone = lastDone;
        this.daysInterval = daysInterval;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getHabitID() {
        return habitID;
    }

    public void setHabitID(Integer habitID) {
        this.habitID = habitID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Date getLastDone() {
        return lastDone;
    }

    public void setLastDone(Date lastDone) {
        this.lastDone = lastDone;
    }

    public Integer getDaysInterval() {
        return daysInterval;
    }

    public void setDaysInterval(Integer daysInterval) {
        this.daysInterval = daysInterval;
    }
}
