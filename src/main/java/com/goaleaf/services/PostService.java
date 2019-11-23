package com.goaleaf.services;

import com.goaleaf.entities.Post;

public interface PostService {

    Iterable<Post> getAllHabitPosts(Integer habitID);

    Post findOneByID(Integer id);

    void removePostFromDatabase(Integer id);

    void updatePost(Post post);

    void save(Post post);

    void updatePostImage(Integer postID, String imgName);
}
