package com.goaleaf.entities.viewModels;

import com.goaleaf.entities.enums.Frequency;

import java.util.Date;

public class TaskViewModel {

    private Integer id;

    private String creator;

    private String description;

    private Integer points;

    private Frequency frequency;

    private Integer daysInterval;

    private Date refreshDate;

    private Boolean active;

    private String executor;

    public TaskViewModel(Integer id, String creator, String description, Integer points, Frequency frequency, Integer daysInterval, Date refreshDate, Boolean active, String executor) {
        this.id = id;
        this.creator = creator;
        this.description = description;
        this.points = points;
        this.frequency = frequency;
        this.daysInterval = daysInterval;
        this.refreshDate = refreshDate;
        this.active = active;
        this.executor = executor;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public Integer getDaysInterval() {
        return daysInterval;
    }

    public void setDaysInterval(Integer daysInterval) {
        this.daysInterval = daysInterval;
    }

    public Date getRefreshDate() {
        return refreshDate;
    }

    public void setRefreshDate(Date refreshDate) {
        this.refreshDate = refreshDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }
}
