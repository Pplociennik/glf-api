package com.goaleaf.services;

import com.goaleaf.entities.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface PostService {

    Iterable<Post> getAllHabitPosts(Integer habitID);

    Post findOneByID(Integer id);

    void removePostFromDatabase(Integer id);

    void updatePost(Post post);

    void save(Post post);

    void updatePostImage(Integer postID, String imgName);

    File getPostPicture(Integer postID);
}
