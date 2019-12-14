package com.goaleaf.repositories;

import com.goaleaf.entities.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PostRepository extends CrudRepository<Post, Integer> {

    Iterable<Post> getAllByHabitIDOrderByDateOfAdditionDesc(Integer habitID);

    Post findById(Integer id);

    @Override
    void delete(Iterable<? extends Post> entities);

    Iterable<Post> findAllByCreatorLogin(String creatorLogin);
}
