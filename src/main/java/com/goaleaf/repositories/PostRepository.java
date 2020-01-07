package com.goaleaf.repositories;

import com.goaleaf.entities.Post;
import com.goaleaf.entities.enums.PostTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PostRepository extends CrudRepository<Post, Integer>, PagingAndSortingRepository<Post, Integer> {

    Iterable<Post> getAllByHabitIDOrderByDateOfAdditionDesc(Integer habitID);

    Post findById(Integer id);

    @Override
    void delete(Iterable<? extends Post> entities);

    Iterable<Post> findAllByCreatorLogin(String creatorLogin);

    Page<Post> findAllByHabitIDAndPostType(Integer habitID, PostTypes type, Pageable pageable);

    Page<Post> findAllByHabitIDAndPostType(Integer habitID, PostTypes type);

}
