package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.Notification;
import com.goaleaf.repositories.NotificationRepository;
import com.goaleaf.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Iterable<Notification> getAllByUserID(Integer userID) {
        return notificationRepository.getAllByRecipientID(userID);
    }


    @Override
    public Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    @Override
    public Iterable<Notification> getAll() {
        return notificationRepository.findAll();
    }

    @Override
    public void removeFromDatabaseByID(Integer ntfID) {
        notificationRepository.delete(ntfID);
    }

    @Override
    public Notification findSpecifiedNtf(Integer userID, String url) {
        return notificationRepository.getByRecipientIDAndUrl(userID, url);
    }
}
