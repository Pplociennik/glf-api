package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.Comment;
import com.goaleaf.repositories.CommentRepository;
import com.goaleaf.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Iterable<Comment> listAllByPostID(Integer postID) {
        return commentRepository.getAllByPostIDOrderByCreationDateDesc(postID);
    }

    @Override
    public Comment getOneByID(Integer commentID) {
        return commentRepository.findById(commentID);
    }

    @Override
    public Comment addNewComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public void removeById(Integer id) {
        commentRepository.delete(id);
    }

    @Override
    public void updateComment(Comment comment) {
        commentRepository.save(comment);
    }
}
