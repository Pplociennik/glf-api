package com.goaleaf.entities;

import javax.persistence.*;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer userID;

    @Column
    private Integer habitID;

    @Column
    private String userLogin;

    @Column
    private String imgName;

    @Column
    private Integer points;

//    @Column
//    @ManyToMany
//    private Set<Habit> habits;

//    @OneToMany(mappedBy = "members")
//    @Column
//    private Set<LeafDate> doneDates;

    public Member(Integer userID /*, Set<LeafDate> doneDates*/) {
        this.userID = userID;
//        this.doneDates = doneDates;
    }

    public Member() {
    }

    public Member(Integer userID, Integer habitID) {
        this.userID = userID;
        this.habitID = habitID;
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

    //    public Set<LeafDate> getDoneDates() {
//        return doneDates;
//    }
//
//    public void setDoneDates(Set<LeafDate> doneDates) {
//        this.doneDates = doneDates;
//    }
//
//    public void addDate(Date date) {
//        this.doneDates.add(new LeafDate(id, new Date()));
//    }


    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void addPoints(Integer increaseNr) {
        this.points += increaseNr;
    }

    public void decreasePoints(Integer decreaseNr) {
        this.points -= decreaseNr;
    }
}
