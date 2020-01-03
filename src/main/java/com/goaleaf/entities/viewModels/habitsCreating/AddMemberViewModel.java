package com.goaleaf.entities.viewModels.habitsCreating;

public class AddMemberViewModel {

    private String token;

    private Integer habitID;

    private String userLogin;

    private String url;

    public AddMemberViewModel() {
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

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
