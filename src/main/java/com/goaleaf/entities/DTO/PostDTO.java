package com.goaleaf.entities.DTO;

import com.goaleaf.entities.enums.PostTypes;

import java.util.Date;

public class PostDTO {

    private Integer id;

    private String creator;

    private String text;

    private PostTypes type;

    private Date dateOfAddition;

    public PostDTO() {
    }

    public PostDTO(String creator, String text, PostTypes type, Date dateOfAddition) {
        this.creator = creator;
        this.text = text;
        this.type = type;
        this.dateOfAddition = dateOfAddition;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PostTypes getType() {
        return type;
    }

    public void setType(PostTypes type) {
        this.type = type;
    }

    public Date getDateOfAddition() {
        return dateOfAddition;
    }

    public void setDateOfAddition(Date dateOfAddition) {
        this.dateOfAddition = dateOfAddition;
    }
}
