package com.goaleaf.services;

import com.goaleaf.entities.DTO.PostDTO;
import com.goaleaf.entities.DTO.PostReactionsNrDTO;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.viewModels.habitsManaging.postsCreating.NewPostViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.AddReactionViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.EditPostViewModel;
import org.springframework.http.HttpStatus;

import java.io.File;

public interface PostService {

    Iterable<PostDTO> getAllHabitPosts(Integer habitID);

    PostDTO findOneByID(Integer id);

    void removePostFromDatabase(Integer id);

    void updatePost(Post post);

    void save(Post post);

    void updatePostImage(Integer postID, String imgName);

    File getPostPicture(Integer postID);

    String getPostImageCode(Integer postID);

    PostDTO addNewPost(NewPostViewModel model);

    HttpStatus editPost(EditPostViewModel model);

    PostReactionsNrDTO addReaction(AddReactionViewModel model);
}
