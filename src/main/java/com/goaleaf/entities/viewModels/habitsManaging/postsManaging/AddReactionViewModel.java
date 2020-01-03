package com.goaleaf.entities.viewModels.habitsManaging.postsManaging;

public class AddReactionViewModel {

    private Integer postID;

    private String token;

    private String type;

    public AddReactionViewModel(Integer postID, String token, String type) {
        this.postID = postID;
        this.token = token;
        this.type = type;
    }

    public AddReactionViewModel() {
    }

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
