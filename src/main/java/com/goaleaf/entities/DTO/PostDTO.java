package com.goaleaf.entities.DTO;

import com.goaleaf.entities.enums.PostTypes;

import java.util.Date;

public class PostDTO {

    private Integer id;

    private Integer habitID;

    private String creatorLogin;

    private PostTypes postType;

    private String postText;

    private String imageCode;

    private String creatorImage;

    private Integer counter_CLAPPING;

    private Integer counter_WOW;

    private Integer counter_NS;

    private Integer counter_TTD;

    private Date dateOfAddition;

    private String userComment;

    private Integer taskPoints;

    private Integer taskID;

    public PostDTO(Builder builder) {
        this.dateOfAddition = builder.dateOfAddition;
        this.counter_CLAPPING = builder.counter_CLAPPING;
        this.counter_NS = builder.counter_NS;
        this.counter_TTD = builder.counter_TTD;
        this.counter_WOW = builder.counter_WOW;
        this.creatorImage = builder.creatorImage;
        this.creatorLogin = builder.creatorLogin;
        this.habitID = builder.habitID;
        this.id = builder.id;
        this.imageCode = builder.imageCode;
        this.postText = builder.postText;
        this.postType = builder.postType;
        this.taskID = builder.taskID;
        this.taskPoints = builder.taskPoints;
        this.userComment = builder.userComment;
    }

    public Integer getId() {
        return id;
    }

    public Integer getHabitID() {
        return habitID;
    }

    public String getCreatorLogin() {
        return creatorLogin;
    }

    public PostTypes getPostType() {
        return postType;
    }

    public String getPostText() {
        return postText;
    }

    public String getImageCode() {
        return imageCode;
    }

    public String getCreatorImage() {
        return creatorImage;
    }

    public Integer getCounter_CLAPPING() {
        return counter_CLAPPING;
    }

    public Integer getCounter_WOW() {
        return counter_WOW;
    }

    public Integer getCounter_NS() {
        return counter_NS;
    }

    public Integer getCounter_TTD() {
        return counter_TTD;
    }

    public Date getDateOfAddition() {
        return dateOfAddition;
    }

    public String getUserComment() {
        return userComment;
    }

    public Integer getTaskPoints() {
        return taskPoints;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public static class Builder {
        private Integer id;
        private Integer habitID;
        private String creatorLogin;
        private PostTypes postType;
        private String postText;
        private String imageCode;
        private String creatorImage;
        private Integer counter_CLAPPING;
        private Integer counter_WOW;
        private Integer counter_NS;
        private Integer counter_TTD;
        private Date dateOfAddition;
        private String userComment;
        private Integer taskPoints;
        private Integer taskID;

        public Builder setId(Integer id) {
            this.id = id;
            return this;
        }

        public Builder setHabitID(Integer habitID) {
            this.habitID = habitID;
            return this;
        }

        public Builder setCreatorLogin(String creatorLogin) {
            this.creatorLogin = creatorLogin;
            return this;
        }

        public Builder setPostType(PostTypes postType) {
            this.postType = postType;
            return this;
        }

        public Builder setPostText(String postText) {
            this.postText = postText;
            return this;
        }

        public Builder setImageCode(String imageCode) {
            this.imageCode = imageCode;
            return this;
        }

        public Builder setCreatorImage(String creatorImage) {
            this.creatorImage = creatorImage;
            return this;
        }

        public Builder setCounter_CLAPPING(Integer counter_CLAPPING) {
            this.counter_CLAPPING = counter_CLAPPING;
            return this;
        }

        public Builder setCounter_WOW(Integer counter_WOW) {
            this.counter_WOW = counter_WOW;
            return this;
        }

        public Builder setCounter_NS(Integer counter_NS) {
            this.counter_NS = counter_NS;
            return this;
        }

        public Builder setCounter_TTD(Integer counter_TTD) {
            this.counter_TTD = counter_TTD;
            return this;
        }

        public Builder setDateOfAddition(Date dateOfAddition) {
            this.dateOfAddition = dateOfAddition;
            return this;
        }

        public Builder setUserComment(String userComment) {
            this.userComment = userComment;
            return this;
        }

        public Builder setTaskPoints(Integer taskPoints) {
            this.taskPoints = taskPoints;
            return this;
        }

        public Builder setTaskID(Integer taskID) {
            this.taskID = taskID;
            return this;
        }

        public PostDTO createDTO() {
            return new PostDTO(this);
        }
    }
}
