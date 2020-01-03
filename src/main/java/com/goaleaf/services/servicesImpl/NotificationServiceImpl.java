package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.NotificationDTO;
import com.goaleaf.entities.Notification;
import com.goaleaf.repositories.NotificationRepository;
import com.goaleaf.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public NotificationServiceImpl() {
    }

    @Override
    public Iterable<NotificationDTO> getAllByUserID(Integer userID) {
        return convertManyToDTOs(notificationRepository.getAllByRecipientID(userID));
    }


    @Override
    public NotificationDTO saveNotification(Notification notification) {
        return convertOneToDTO(notificationRepository.save(notification));
    }

    @Override
    public Iterable<NotificationDTO> getAll() {
        return convertManyToDTOs(notificationRepository.findAll());
    }

    @Override
    public void removeFromDatabaseByID(Integer ntfID) {
        notificationRepository.delete(ntfID);
    }

    @Override
    public NotificationDTO findSpecifiedNtf(Integer userID, String url) {
        return convertOneToDTO(notificationRepository.getByRecipientIDAndUrl(userID, url));
    }

    @Override
    public NotificationDTO findByDescription(String description) {
        return convertOneToDTO(notificationRepository.findByDescription(description));
    }

    private NotificationDTO convertOneToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();

        dto.setDate(notification.getDate());
        dto.setDescription(notification.getDescription());
        dto.setId(notification.getId());
        dto.setRecipientID(notification.getRecipientID());
        dto.setUrl(notification.getUrl());

        return dto;
    }

    private Iterable<NotificationDTO> convertManyToDTOs(Iterable<Notification> input) {
        List<NotificationDTO> output = new ArrayList<>(0);

        for (Notification n : input) {
            output.add(convertOneToDTO(n));
        }

        return output;
    }
}
