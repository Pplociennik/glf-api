package com.goaleaf.services;

import com.goaleaf.entities.Comment;
import org.springframework.stereotype.Service;

@Service
public interface CommentService {

    Iterable<Comment> listAllByPostID(Integer postID);

    Comment getOneByID(Integer commentID);

    Comment addNewComment(Comment comment);

    void removeById(Integer id);

    void updateComment(Comment comment);
}
