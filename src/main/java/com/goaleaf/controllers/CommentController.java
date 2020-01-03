package com.goaleaf.controllers;

import com.goaleaf.entities.Comment;
import com.goaleaf.entities.DTO.CommentDTO;
import com.goaleaf.entities.Stats;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.commentsCreating.AddCommentViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.commentsManaging.EditCommentViewModel;
import com.goaleaf.services.CommentService;
import com.goaleaf.services.PostService;
import com.goaleaf.services.StatsService;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.PostNotFoundException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.commentsProcessing.CommentNotFoundException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.commentsProcessing.EmptyCommentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;
    @Autowired
    private StatsService statsService;

    @RequestMapping(value = "/addcomment", method = RequestMethod.POST)
    public CommentDTO addComment(@RequestBody AddCommentViewModel model) {

        if (postService.findOneByID(model.getPostID()) == null)
            throw new PostNotFoundException("Post not found");
        if (model.getText().trim().isEmpty())
            throw new EmptyCommentException("Comment cannot be empty!");

        CommentDTO commentDTO = commentService.addNewComment(model);

        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseCommentedPosts();
        statsService.save(stats);

        return commentDTO;
    }

    @RequestMapping(value = "/getcomments", method = RequestMethod.GET)
    public Iterable<CommentDTO> getAllPostComments(@RequestParam Integer postID) {
        if (postService.findOneByID(postID) == null)
            throw new PostNotFoundException("Post not found!");
        return commentService.listAllByPostID(postID);
    }

    @RequestMapping(value = "/remove", method = RequestMethod.DELETE)
    public void removeCommentByID(@RequestParam Integer id) {
        commentService.removeById(id);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET)
    public void updateComment(@RequestBody EditCommentViewModel model) {

        if (commentService.getOneByID(model.commentID) == null)
            throw new CommentNotFoundException("Comment Not Found!");

        Comment comment = commentService.getOneByID(model.commentID);

        comment.setCommentText(model.text);
        commentService.updateComment(comment);
    }
}
