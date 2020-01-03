package com.goaleaf.entities.DTO;


public class MemberDTO {

    private Integer id;

    private Integer userID;

    private Integer habitID;

    private String userLogin;

    private String imageCode;

    private Integer points;

    public MemberDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
