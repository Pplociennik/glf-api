package com.goaleaf.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.goaleaf.entities.enums.Frequency;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "refId", scope = Task.class)
@Entity
@Table(name = "Tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer creatorID;

    private Integer habitID;

    private String description;

    private Integer points;

    private Boolean isCompleted;

    private Frequency frequency;

    private Date lastDone;

    private String executor;

    private Integer executorID;

    private Integer daysInterval;

    private Date creationDate;


    public Task() {
    }

    public Task(Integer creatorID, Integer habitID, String description, Integer points, Boolean isCompleted, Frequency frequency, Integer executorID, Integer daysInterval) {
        this.creatorID = creatorID;
        this.habitID = habitID;
        this.description = description;
        this.points = points;
        this.isCompleted = isCompleted;
        this.frequency = frequency;

        Calendar c = Calendar.getInstance();

        c.setTime(new Date());
        c.add(Calendar.DATE, -20);
        Date refreshDate = c.getTime();

        this.lastDone = refreshDate;
        this.executor = "";
        this.executorID = executorID;
        this.daysInterval = daysInterval;
        this.creationDate = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
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

    public Boolean getCompleted() {
        return isCompleted;
    }

    public void setCompleted(Boolean completed) {
        isCompleted = completed;
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

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public Integer getExecutorID() {
        return executorID;
    }

    public void setExecutorID(Integer executorID) {
        this.executorID = executorID;
    }

    public Integer getDaysInterval() {
        return daysInterval;
    }

    public void setDaysInterval(Integer daysInterval) {
        this.daysInterval = daysInterval;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
