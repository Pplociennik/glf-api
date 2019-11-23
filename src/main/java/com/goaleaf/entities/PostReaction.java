package com.goaleaf.entities;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.goaleaf.entities.enums.Reactions;

import javax.persistence.*;
import java.util.Objects;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "refId", scope = PostReaction.class)
@Entity
@Table(name = "reactions")
public class PostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column
    private Integer postID;

    @Column
    private String userLogin;

    @Column
    private Reactions type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public Reactions getType() {
        return type;
    }

    public void setType(String type) {
        if (Objects.equals(type, "CLAPPING"))
            this.type = Reactions.Clapping;
        if (Objects.equals(type, "WOW"))
            this.type = Reactions.Wow;
        if (Objects.equals(type, "NOTHING_SPECIAL"))
            this.type = Reactions.Nothing_Special;
        if (Objects.equals(type, "THERES_THE_DOOR"))
            this.type = Reactions.Theres_The_Door;
    }
}
