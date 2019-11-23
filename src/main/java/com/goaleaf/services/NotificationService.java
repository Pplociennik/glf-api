package com.goaleaf.services;

import com.goaleaf.entities.Notification;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    Iterable<Notification> getAllByUserID(Integer userID);

    Notification saveNotification(Notification notification);

    Iterable<Notification> getAll();

    void removeFromDatabaseByID(Integer ntfID);

    Notification findSpecifiedNtf(Integer userID, String url);

}
