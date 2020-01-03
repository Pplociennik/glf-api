package com.goaleaf.entities.viewModels.habitsManaging;

public class JoinHabitViewModel {

    private String token;

    private Integer userID;

    private Integer habitID;

    public JoinHabitViewModel() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getHabitID() {
        return habitID;
    }

    public void setHabitID(Integer habitID) {
        this.habitID = habitID;
    }
}
