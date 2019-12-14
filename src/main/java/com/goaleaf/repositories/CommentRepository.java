package com.goaleaf.repositories;

import com.goaleaf.entities.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CommentRepository extends CrudRepository<Comment, Integer> {

    Iterable<Comment> getAllByPostIDOrderByCreationDateDesc(Integer postID);

    Comment findById(Integer commentID);

    @Override
    void delete(Iterable<? extends Comment> entities);

    Iterable<Comment> findAllByUserID(Integer userID);

    //    Comment save(Comment comment);
}
