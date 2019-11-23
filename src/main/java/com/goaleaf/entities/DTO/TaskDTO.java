package com.goaleaf.entities.DTO;

import com.goaleaf.entities.enums.Frequency;

import java.util.Date;

public class TaskDTO {

    private String token;

    private Integer habitID;

    private String description;

    private Integer points;

    private Frequency frequency;

    private Date lastDone;

    private Integer daysInterval;

    public TaskDTO() {
    }

    public TaskDTO(String token, Integer habitID, String description, Integer points, Frequency frequency, Date lastDone, Integer daysInterval) {
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

    public Integer getHabitID() {
        return habitID;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPoints() {
        return points;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public Date getLastDone() {
        return lastDone;
    }

    public Integer getDaysInterval() {
        return daysInterval;
    }
}
