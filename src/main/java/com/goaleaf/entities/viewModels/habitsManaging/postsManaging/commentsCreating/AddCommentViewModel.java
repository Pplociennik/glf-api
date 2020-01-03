package com.goaleaf.entities.viewModels.habitsManaging.postsManaging.commentsCreating;

public class AddCommentViewModel {

    private Integer postID;

    private Integer creatorID;

    private String text;

    public AddCommentViewModel() {
    }

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public Integer getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Integer creatorID) {
        this.creatorID = creatorID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
