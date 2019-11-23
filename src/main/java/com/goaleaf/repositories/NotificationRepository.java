package com.goaleaf.repositories;

import com.goaleaf.entities.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Integer> {

    Iterable<Notification> getAllByRecipientID(Integer userID);

    Iterable<Notification> findAll();

    Notification getByRecipientIDAndUrl(Integer userID, String url);

//    Notification save(Notification notification);
}
