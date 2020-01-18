package com.goaleaf.controllers;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.DTO.*;
import com.goaleaf.entities.DTO.pagination.PostPageDTO;
import com.goaleaf.entities.PostReaction;
import com.goaleaf.entities.enums.PostTypes;
import com.goaleaf.entities.viewModels.habitsManaging.postsCreating.NewPostViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.AddReactionViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.EditPostViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.RemovePostViewModel;
import com.goaleaf.services.*;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import com.goaleaf.validators.exceptions.habitsProcessing.MemberDoesNotExistException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.PostNotFoundException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.UserIsNotCreatorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;

import static com.goaleaf.security.SecurityConstants.SECRET;


@RestController
@RequestMapping(value = "/api/posts")
@CrossOrigin(value = "https://goaleaf.com", maxAge = 3600)
public class PostController {

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PostService postService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private UserService userService;
    @Autowired
    private ReactionService reactionService;
    @Autowired
    private StatsService statsService;
    @Autowired
    private HabitService habitService;

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<PostDTO> getAllHabitPosts(@RequestParam String token, Integer habitID) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();

        if (!jwtService.Validate(token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (memberService.findSpecifiedMember(habitID, Integer.parseInt(claims.getSubject())) == null)
            throw new MemberDoesNotExistException("You are not a member!");

        return postService.getAllHabitPosts(habitID);
    }

    @RequestMapping(value = "/addpost", method = RequestMethod.POST)
    public PostDTO addNewPost(@RequestBody NewPostViewModel model) {
        return postService.addNewPost(model);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public HttpStatus removePostFromDatabase(@RequestBody RemovePostViewModel model) {

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.token).getBody();

        UserDTO tempUser = userService.findById(Integer.parseInt(claims.getSubject()));
        HabitDTO habitDTO = habitService.findById(model.habitID);
        PostDTO postDTO = postService.findOneByID(model.postID);

        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (memberService.findSpecifiedMember(model.habitID, Integer.parseInt(claims.getSubject())) == null)
            throw new MemberDoesNotExistException("You are not a member!");
        if (!postDTO.getCreatorLogin().equals(tempUser.getLogin()) && habitDTO.getCreatorID().compareTo(Integer.parseInt(claims.getSubject())) != 0)
            throw new UserIsNotCreatorException("You cannot delete posts which were not posted by you!");

        postService.removePostFromDatabase(model.postID);
        return HttpStatus.OK;

    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.PUT)
    public HttpStatus editPost(@RequestBody EditPostViewModel model) {
        return postService.editPost(model);
    }

    @RequestMapping(value = "/post/addreaction", method = RequestMethod.POST)
    public PostReactionsNrDTO addReactionToPost(@RequestBody AddReactionViewModel model) {
        return postService.addReaction(model);
    }

    @RequestMapping(value = "/presentreaction", method = RequestMethod.GET)
    public PostReaction getUsersPresentReaction(@RequestParam Integer postID, @RequestParam String userLogin) throws AccountNotExistsException {
        if (postService.findOneByID(postID) == null)
            throw new PostNotFoundException("Post not found");
        if (userService.findByLogin(userLogin) == null) {
            throw new AccountNotExistsException("Account with this login not exists!");
        }

        return reactionService.getReactionByPostIdAndUserLogin(postID, userLogin);
    }

    @GetMapping(value = "/type/paging")
    public PostPageDTO getAllByTypePaging(@RequestParam Integer pageNr, @RequestParam Integer objectsNr, @RequestParam Integer habitID, @RequestParam PostTypes type) {
        return postService.getAllByTypePaging(pageNr, objectsNr, habitID, type);
    }
}
