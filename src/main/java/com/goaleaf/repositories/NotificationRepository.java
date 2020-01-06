package com.goaleaf.repositories;

import com.goaleaf.entities.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer>, PagingAndSortingRepository<Notification, Integer> {

    Iterable<Notification> getAllByRecipientID(Integer userID);

    Iterable<Notification> findAll();

    Notification findByRecipientIDAndUrl(Integer userID, String url);

    Notification findByDescription(String description);

    @Override
    void delete(Iterable<? extends Notification> entities);

    //    Notification save(Notification notification);

    Page<Notification> findAllByRecipientID(Integer recipientID, Pageable pageable);
}
