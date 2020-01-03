package com.goaleaf.repositories;

import com.goaleaf.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface UserRepository extends CrudRepository<User, Integer> {

    User findById(Integer Id);

    @Query("select count(*) from User p where p.id = ?1")
    Integer checkIfExists(Integer id);

    User findByEmailAddress(String emailAddress);

    User findByLogin(String login);

    Boolean existsByEmailAddress(String email);

    Boolean existsByLogin(String login);

}
