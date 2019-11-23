package com.goaleaf.services;

import com.goaleaf.entities.PostReaction;
import org.springframework.stereotype.Service;

@Service
public interface ReactionService {

    Iterable<PostReaction> listAllByPostID(Integer postID);

    Iterable<PostReaction> listAllByPostIDAndType(Integer postID, String type);

    PostReaction getReactionByPostIdAndUserLogin(Integer postID, String userLogin);

    boolean checkIfExist(Integer postID, String userLogin);

    void remove(Integer postID, String userLogin);

    void add(PostReaction reaction);

    void delete(PostReaction reaction);
}
