package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.Comment;
import com.goaleaf.entities.DTO.CommentDTO;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.PostDTO;
import com.goaleaf.entities.DTO.UserDTO;
import com.goaleaf.entities.DTO.pagination.CommentPageDto;
import com.goaleaf.entities.Notification;
import com.goaleaf.entities.viewModels.habitsManaging.postsManaging.commentsCreating.AddCommentViewModel;
import com.goaleaf.repositories.CommentRepository;
import com.goaleaf.security.EmailNotificationsSender;
import com.goaleaf.services.CommentService;
import com.goaleaf.services.HabitService;
import com.goaleaf.services.PostService;
import com.goaleaf.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private HabitService habitService;

    @Override
    public Iterable<CommentDTO> listAllByPostID(Integer postID) {
        return convertManyToDTOs(commentRepository.getAllByPostIDOrderByCreationDateDesc(postID));
    }

    @Override
    public CommentPageDto getAllPostCommentsPaging(Integer pageNr, Integer objectsNr, Integer postID) {
        Pageable pageable = new PageRequest(pageNr, objectsNr);
        List<Comment> input = (List<Comment>) commentRepository.findAllByPostID(postID);

//        Collections.sort(input, new Comparator<Comment>() {
//            @Override
//            public int compare(Comment a, Comment b) {
//                return a.getCreationDate().compareTo(b.getCreationDate());
//            }
//        });

        List<CommentDTO> list = (List<CommentDTO>) convertManyToDTOs(input);

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > list.size() ? list.size() : (start + pageable.getPageSize());
        Page<CommentDTO> pages = new PageImpl<CommentDTO>(list.subList(start, end), pageable, list.size());

        return new CommentPageDto(pages.getContent(), pages.getNumber(), pages.hasPrevious(), pages.hasNext(), pages.getTotalPages());
    }

    @Override
    public Comment getOneByID(Integer commentID) {
        return commentRepository.findById(commentID);
    }

    @Override
    public CommentDTO addNewComment(AddCommentViewModel model) {

        UserDTO commenter = userService.findById(model.getCreatorID());
        PostDTO post = postService.findOneByID(model.getPostID());
        UserDTO postCreator = userService.findByLogin(post.getCreatorLogin());
        HabitDTO habitDTO = habitService.findById(post.getHabitID());

        Comment comment = new Comment();
        comment.setCommentText(model.getText());
        comment.setPostID(model.getPostID());
        comment.setUserID(model.getCreatorID());
        comment.setUserLogin(userService.findById(model.getCreatorID()).getLogin());
        comment.setCreationDate(new Date());
        comment.setCreatorImage(userService.findById(model.getCreatorID()).getImageCode());

        Comment returned = commentRepository.save(comment);

        CommentDTO commentDTO = convertOneToDTO(comment);

        if (postCreator.getUserID() != returned.getUserID()) {
            if (postCreator.getNotifications() && postCreator.getUserID() != returned.getUserID()) {

                String ntfDesc = commenter.getLogin() + " commented on your post in challenge \"" + habitDTO.getTitle() + "\"";
                Notification ntf = new EmailNotificationsSender().createInAppNotification(postCreator.getUserID(), ntfDesc, "http://www.goaleaf.com/challenge/" + post.getHabitID(), false);
                EmailNotificationsSender sender = new EmailNotificationsSender();
                try {
                    sender.postCommented(postCreator.getEmailAddress(), postCreator.getLogin(), comment.getUserLogin(), post, comment);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
        return commentDTO;
    }

    @Override
    public void removeById(Integer id) {
        commentRepository.delete(id);
    }

    @Override
    public void updateComment(Comment comment) {
        commentRepository.save(comment);
    }

    private CommentDTO convertOneToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();

        commentDTO.setCreatorID(comment.getUserID());
        commentDTO.setPostID(comment.getPostID());
        commentDTO.setText(comment.getCommentText());
        commentDTO.setCreatorLogin(comment.getUserLogin());
        commentDTO.setCreationDate(comment.getCreationDate());
        commentDTO.setCreatorImage(comment.getCreatorImage());
        commentDTO.setId(comment.getId());

        return commentDTO;
    }

    private Iterable<CommentDTO> convertManyToDTOs(Iterable<Comment> input) {
        List<CommentDTO> output = new ArrayList<>(0);

        for (Comment c : input) {
            output.add(convertOneToDTO(c));
        }
        return output;
    }
}
