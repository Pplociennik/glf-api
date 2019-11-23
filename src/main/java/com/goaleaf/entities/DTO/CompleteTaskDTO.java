package com.goaleaf.entities.DTO;

public class CompleteTaskDTO {

    private Integer habitID;

    private String token;

    private Integer taskID;

    private String comment;


    public CompleteTaskDTO() {
    }

    public CompleteTaskDTO(Integer habitID, String token, Integer taskID, String comment) {
        this.habitID = habitID;
        this.token = token;
        this.taskID = taskID;
        this.comment = comment;
    }

    public Integer getHabitID() {
        return habitID;
    }

    public String getToken() {
        return token;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public String getComment() {
        return comment;
    }

    public void setHabitID(Integer habitID) {
        this.habitID = habitID;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}

