package com.goaleaf.services.servicesImpl;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.goaleaf.entities.*;
import com.goaleaf.entities.DTO.*;
import com.goaleaf.entities.DTO.pagination.PostPageDTO;
import com.goaleaf.entities.enums.PostTypes;
import com.goaleaf.entities.viewModels.habitsManaging.postsCreating.NewPostViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.AddReactionViewModel;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.EditPostViewModel;
import com.goaleaf.repositories.CommentRepository;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.repositories.PostRepository;
import com.goaleaf.repositories.TaskRepository;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.*;
import com.goaleaf.validators.FileConverter;
import com.goaleaf.validators.exceptions.habitsProcessing.MemberDoesNotExistException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.EmptyPostException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.PostNotFoundException;
import com.goaleaf.validators.exceptions.habitsProcessing.postsProcessing.UserIsNotCreatorException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import javax.mail.MessagingException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.goaleaf.security.SecurityConstants.SECRET;

public class PostServiceImpl implements PostService {

    @Autowired
    JwtService jwtService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private HabitService habitService;

    @Autowired
    private StatsService statsService;

    @Autowired
    private ReactionService reactionService;

    @Override
    public Iterable<PostDTO> getAllHabitPosts(Integer habitID) {
        return convertManyToDTOs(postRepository.getAllByHabitIDOrderByDateOfAdditionDesc(habitID));
    }

    @Override
    public PostDTO findOneByID(Integer id) {
        return convertOneToDTO(postRepository.findById(id));
    }

    @Override
    public void removePostFromDatabase(Integer id) {
        Post post = postRepository.findById(id);

        if (post.getPostType().equals(PostTypes.Task)) {
            Task task = taskRepository.getById(id);
            Member member = memberRepository.findByHabitIDAndUserID(task.getHabitID(), task.getExecutorID());

            member.decreasePoints(task.getPoints());

            memberRepository.save(member);
        }

        if (commentRepository.getAllByPostIDOrderByCreationDateDesc(id).iterator().hasNext()) {
            Iterable<Comment> comments = commentRepository.getAllByPostIDOrderByCreationDateDesc(id);
            commentRepository.delete(comments);
            if (commentRepository.getAllByPostIDOrderByCreationDateDesc(id).iterator().hasNext()) {
                throw new RuntimeException("Comments were not deleted properly!");
            }
        }

        postRepository.delete(id);
    }

    @Override
    public void updatePost(Post post) {
        postRepository.save(post);
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public void updatePostImage(Integer postID, String imgName) {
        Post post = postRepository.findById(postID);
        post.setImageCode(imgName);
        if (post.getPostText().isEmpty())
            post.setPostType(PostTypes.JustPhoto);
        else
            post.setPostType(PostTypes.TextAndPhoto);
        postRepository.save(post);
    }

    @Override
    public File getPostPicture(Integer postID) {
        Post post = postRepository.findById(postID);
        if (post.getImageCode() == null || post.getImageCode().trim().isEmpty()) {
            throw new RuntimeException("This post has no image!");
        }
        return FileConverter.decodeFileFromBase64Binary(post.getImageCode());
    }

    @Override
    public String getPostImageCode(Integer postID) {
        if (postRepository.findById(postID) == null) {
            throw new RuntimeException("Post does not exist!");
        }
        if (postRepository.findById(postID).getPostType().equals(PostTypes.JustText) || postRepository.findById(postID).getPostType().equals(PostTypes.HabitFinished) || postRepository.findById(postID).getPostType().equals(PostTypes.Task)) {
            throw new RuntimeException("This post does not contain any photo!");
        }

        Post post = postRepository.findById(postID);
        return post.getImageCode();
    }

    @Override
    public PostDTO addNewPost(NewPostViewModel model) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.token).getBody();

        UserDTO tempUser = userService.findById(Integer.parseInt(claims.getSubject()));

        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (memberService.findSpecifiedMember(model.habitID, Integer.parseInt(claims.getSubject())) == null)
            throw new MemberDoesNotExistException("You are not a member!");
        if (model.postText.trim().isEmpty())
            throw new EmptyPostException("Post cannot be empty!");
        if (model.postText.length() > 300) {
            throw new RuntimeException("Post cannot be longer than 600 characters!");
        }

        Post newPost = new Post();
        newPost.setHabitID(model.habitID);
        if (model.type == PostTypes.JustText)
            newPost.setPostType(PostTypes.JustText);
        if (model.type == PostTypes.JustPhoto)
            newPost.setPostType(PostTypes.JustPhoto);
        if (model.type == PostTypes.TextAndPhoto)
            newPost.setPostType(PostTypes.TextAndPhoto);
        newPost.setCreatorLogin(tempUser.getLogin());
        newPost.setPostText(model.postText);
        newPost.setDateOfAddition(new Date());
        newPost.setCreatorImage(tempUser.getImageCode());

        if (model.type.equals(PostTypes.JustPhoto) || model.type.equals(PostTypes.TextAndPhoto)) {
            newPost.setImageCode(model.imageCode);
        }

        save(newPost);

        Iterable<MemberDTO> members = memberService.getAllByHabitID(model.habitID);
        HabitDTO habit = habitService.findById(model.habitID);

        String ntfDesc = newPost.getCreatorLogin() + " added a new post in challenge \"" + habit.getTitle() + "\"";
        for (MemberDTO m : members) {
            UserDTO u = userService.findById(m.getUserID());
            Notification ntf = new EmailNotificationsSender().createInAppNotification(u.getUserID(), ntfDesc, "http://www.goaleaf.com/habit/" + habit.getId(), false);
            if (u.getNotifications() && !u.getLogin().equals(tempUser.getLogin())) {
                EmailNotificationsSender sender = new EmailNotificationsSender();
                try {
                    sender.postAdded(u.getEmailAddress(), u.getLogin(), newPost.getCreatorLogin(), habit, newPost);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        }


        Stats stats = statsService.findStatsByDate(new Date());
        if (stats == null) {
            stats = new Stats();
        }
        stats.increaseCreatedPosts();
        statsService.save(stats);

//        PostDTO dataToResponse = new PostDTO();
//        dataToResponse.creator = tempUser.getLogin();
//        dataToResponse.text = model.postText;
//        dataToResponse.type = model.type;
//        dataToResponse.dateOfAddition = newPost.getDateOfAddition();

        return convertOneToDTO(newPost);
    }

    @Override
    public HttpStatus editPost(EditPostViewModel model) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.token).getBody();

        UserDTO tempUser = userService.findById(Integer.parseInt(claims.getSubject()));

        if (!jwtService.Validate(model.token, SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (memberService.findSpecifiedMember(model.habitID, Integer.parseInt(claims.getSubject())) == null)
            throw new MemberDoesNotExistException("You are not a member!");
        if (findOneByID(model.postID).getCreatorLogin() != tempUser.getLogin())
            throw new UserIsNotCreatorException("You cannot edit posts which were not posted by you");

        Post post = postRepository.findById(model.postID);
        if (model.type == PostTypes.JustText)
            post.setPostType(PostTypes.JustText);
        if (model.type == PostTypes.JustPhoto)
            post.setPostType(PostTypes.JustPhoto);
        if (model.type == PostTypes.TextAndPhoto)
            post.setPostType(PostTypes.TextAndPhoto);

        post.setPostText(model.text);

        updatePost(post);

        return HttpStatus.OK;
    }

    @Override
    public PostReactionsNrDTO addReaction(AddReactionViewModel model) {
        Post post = postRepository.findById(model.getPostID());
        String pastType = "";
        HabitDTO habit = habitService.findById(post.getHabitID());
        UserDTO postCreator = userService.findByLogin(post.getCreatorLogin());

        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(model.getToken()).getBody();
        UserDTO tempUser = userService.findById(Integer.parseInt(claims.getSubject()));

        if (!jwtService.Validate(model.getToken(), SECRET))
            throw new TokenExpiredException("You have to be logged in!");
        if (memberService.findSpecifiedMember(post.getHabitID(), Integer.parseInt(claims.getSubject())) == null)
            throw new MemberDoesNotExistException("You are not a member!");
        if (postRepository.findById(model.getPostID()) == null)
            throw new PostNotFoundException("Post not found");

        Iterable<MemberDTO> members = memberService.getAllByHabitID(habit.getId());
        String ntfDesc = tempUser.getLogin() + " reacted to your post in challenge \"" + habit.getTitle() + "\"";
        Notification ntf = new EmailNotificationsSender().createInAppNotification(postCreator.getUserID(), ntfDesc, "http://www.goaleaf.com/habit/" + post.getHabitID(), false);
        if (postCreator.getNotifications() && tempUser.getUserID().compareTo(postCreator.getUserID()) != 0) {
            EmailNotificationsSender sender = new EmailNotificationsSender();
            try {
                sender.postReacted(postCreator.getEmailAddress(), postCreator.getLogin(), tempUser.getLogin(), post);
            } catch (MessagingException e) {
                System.out.println("addReaction_method");
                e.printStackTrace();
            }
        }


        if (!(reactionService.getReactionByPostIdAndUserLogin(model.getPostID(), tempUser.getLogin()) == null)) {
            PostReaction reaction = reactionService.getReactionByPostIdAndUserLogin(model.getPostID(), tempUser.getLogin());
            pastType = String.valueOf(reaction.getType()).toUpperCase();
            reactionService.delete(reaction);
            if (Objects.equals(pastType, "CLAPPING"))
                post.setCounter_CLAPPING(post.getCounter_CLAPPING() - 1);
            if (Objects.equals(pastType, "WOW"))
                post.setCounter_WOW(post.getCounter_WOW() - 1);
            if (Objects.equals(pastType, "NOTHING_SPECIAL"))
                post.setCounter_NS(post.getCounter_NS() - 1);
            if (Objects.equals(pastType, "THERES_THE_DOOR"))
                post.setCounter_TTD(post.getCounter_TTD() - 1);
            updatePost(post);
        }
        PostReactionsNrDTO dataToReturn = new PostReactionsNrDTO();
        dataToReturn.setCounter_CLAPPING(post.getCounter_CLAPPING());
        dataToReturn.setCounter_NS(post.getCounter_NS());
        dataToReturn.setCounter_TTD(post.getCounter_TTD());
        dataToReturn.setCounter_WOW(post.getCounter_WOW());


        if (Objects.equals(model.getType(), pastType))
            return dataToReturn;

        PostReaction newReaction = new PostReaction();
        newReaction.setPostID(model.getPostID());
        newReaction.setType(model.getType());
        newReaction.setUserLogin(tempUser.getLogin());

        if (Objects.equals(model.getType(), "CLAPPING"))
            post.setCounter_CLAPPING(post.getCounter_CLAPPING() + 1);
        if (Objects.equals(model.getType(), "WOW"))
            post.setCounter_WOW(post.getCounter_WOW() + 1);
        if (Objects.equals(model.getType(), "NOTHING_SPECIAL"))
            post.setCounter_NS(post.getCounter_NS() + 1);
        if (Objects.equals(model.getType(), "THERES_THE_DOOR"))
            post.setCounter_TTD(post.getCounter_TTD() + 1);

        reactionService.add(newReaction);
        updatePost(post);

        dataToReturn.setCounter_CLAPPING(post.getCounter_CLAPPING());
        dataToReturn.setCounter_NS(post.getCounter_NS());
        dataToReturn.setCounter_TTD(post.getCounter_TTD());
        dataToReturn.setCounter_WOW(post.getCounter_WOW());

        return dataToReturn;
    }

    @Override
    public PostPageDTO getAllByTypePaging(Integer pageNr, Integer objectsNr, Integer habitID, PostTypes type) {
        Pageable pageable = new PageRequest(pageNr, objectsNr);
        List<Post> list = (List<Post>) postRepository.findAllByHabitIDAndPostType(habitID, type);
        List<Post> input = new ArrayList<>(0);

        for (int i = list.size() - 1; i >= 0; i--) {
            input.add(list.get(i));
        }

        List<PostDTO> output = (List<PostDTO>) this.convertManyToDTOs(input);

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > output.size() ? output.size() : (start + pageable.getPageSize());
        Page<PostDTO> pages = new PageImpl<PostDTO>(output.subList(start, end), pageable, output.size());

        return new PostPageDTO(output, pages.getNumber(), pages.hasPrevious(), pages.hasNext(), pages.getTotalPages());
    }

    private PostDTO convertOneToDTO(Post post) {
        PostDTO postDTO = new PostDTO.Builder()
                .setPostText(post.getPostText())
                .setPostType(post.getPostType())
                .setCounter_CLAPPING(post.getCounter_CLAPPING())
                .setCounter_NS(post.getCounter_NS())
                .setCounter_TTD(post.getCounter_TTD())
                .setCounter_WOW(post.getCounter_WOW())
                .setCreatorImage(post.getCreatorImage())
                .setCreatorLogin(post.getCreatorLogin())
                .setDateOfAddition(post.getDateOfAddition())
                .setHabitID(post.getHabitID())
                .setId(post.getId())
                .setImageCode(post.getImageCode())
                .setTaskID(post.getTaskID())
                .setTaskPoints(post.getTaskPoints())
                .setUserComment(post.getUserComment())
                .createDTO();
        return postDTO;
    }

    private Iterable<PostDTO> convertManyToDTOs(Iterable<Post> input) {
        List<PostDTO> output = new ArrayList<>(0);

        for (Post p : input) {
            output.add(convertOneToDTO(p));
        }

        return output;
    }
}
