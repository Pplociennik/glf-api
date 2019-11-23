package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.PostReaction;
import com.goaleaf.repositories.ReactionRepository;
import com.goaleaf.services.ReactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReactionServiceImpl implements ReactionService {

    @Autowired
    private ReactionRepository reactionRepository;


    @Override
    public Iterable<PostReaction> listAllByPostID(Integer postID) {
        return reactionRepository.getAllByPostID(postID);
    }

    @Override
    public Iterable<PostReaction> listAllByPostIDAndType(Integer postID, String type) {
        return reactionRepository.getAllByPostIDAndType(postID, type);
    }

    @Override
    public PostReaction getReactionByPostIdAndUserLogin(Integer postID, String userLogin) {
        return reactionRepository.findByPostIDAndUserLogin(postID, userLogin);
    }

    @Override
    public boolean checkIfExist(Integer postID, String userLogin) {
        return reactionRepository.existsByPostIDAndUserLogin(postID, userLogin);
    }

    @Override
    public void remove(Integer postID, String userLogin) {
        reactionRepository.removeByPostIDAndUserLogin(postID, userLogin);
    }

    @Override
    public void add(PostReaction reaction) {
        reactionRepository.save(reaction);
    }

    @Override
    public void delete(PostReaction reaction) {
        reactionRepository.delete(reaction);
    }
}
