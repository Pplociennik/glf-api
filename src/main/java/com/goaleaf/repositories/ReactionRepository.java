package com.goaleaf.repositories;

import com.goaleaf.entities.PostReaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactionRepository extends CrudRepository<PostReaction, Integer> {

    Iterable<PostReaction> getAllByPostID(Integer postID);

    Iterable<PostReaction> getAllByPostIDAndType(Integer postID, String type);

    PostReaction findByPostIDAndUserLogin(Integer postID, String userLogin);

    boolean existsByPostIDAndUserLogin(Integer postID, String userLogin);

    void removeByPostIDAndUserLogin(Integer postID, String userLogin);

}
