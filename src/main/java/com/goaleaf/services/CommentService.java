package com.goaleaf.services;

import com.goaleaf.entities.Comment;
import com.goaleaf.entities.DTO.CommentDTO;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.commentsCreating.AddCommentViewModel;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

    Iterable<CommentDTO> listAllByPostID(Integer postID);

    Comment getOneByID(Integer commentID);

    CommentDTO addNewComment(AddCommentViewModel model);

    void removeById(Integer id);

    void updateComment(Comment comment);
}
