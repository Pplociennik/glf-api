package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.Member;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.Task;
import com.goaleaf.entities.enums.PostTypes;
import com.goaleaf.repositories.HabitRepository;
import com.goaleaf.repositories.MemberRepository;
import com.goaleaf.repositories.PostRepository;
import com.goaleaf.repositories.TaskRepository;
import com.goaleaf.services.PostService;
import com.goaleaf.validators.FileConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HabitRepository habitRepository;

    @Override
    public Iterable<Post> getAllHabitPosts(Integer habitID) {
        return postRepository.getAllByHabitIDOrderByDateOfAdditionDesc(habitID);
    }

    @Override
    public Post findOneByID(Integer id) {
        return postRepository.findById(id);
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
}
