package com.goaleaf.entities;

import com.goaleaf.entities.enums.Category;
import com.goaleaf.entities.enums.Frequency;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "habits")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private String habitTitle;

    @Column
    private Date habitStartDate;

    @Column
    private Category category;

    @Column
    private Frequency frequency;

//    @Column
//    @OneToMany(mappedBy = "habits")
//    private Set<Member> members;

    @Column
    private Boolean isPrivate;

    @Column
    private Integer creatorID;

    @Column
    private String creatorLogin;

    @Column
    private Integer pointsToWIn;

    @Column
    private String winner;

    @Column
    private Boolean finished;

    public Habit() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHabitTitle() {
        return habitTitle;
    }

    public void setHabitTitle(String habitTitle) {
        this.habitTitle = habitTitle;
    }

    public Date getHabitStartDate() {
        return habitStartDate;
    }

    public void setHabitStartDate(Date habitStartDate) {
        this.habitStartDate = habitStartDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

//    public Set<Member> getMembers() {
//        return members;
//    }
//
//    public void setMembers(Set<Member> members) {
//        this.members = members;
//    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

//    public void addMember(Member newMember) {
//        this.members.add(newMember);
//    }


    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public String getCreatorLogin() {
        return creatorLogin;
    }

    public void setCreatorLogin(String creatorLogin) {
        this.creatorLogin = creatorLogin;
    }

    public Integer getPointsToWIn() {
        return pointsToWIn;
    }

    public void setPointsToWIn(Integer pointsToWIn) {
        this.pointsToWIn = pointsToWIn;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public Boolean getFinished() {
        return finished;
    }

    public void setFinished(Boolean finished) {
        this.finished = finished;
    }
}
