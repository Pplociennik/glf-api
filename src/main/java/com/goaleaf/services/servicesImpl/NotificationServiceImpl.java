package com.goaleaf.services.servicesImpl;

import com.goaleaf.entities.DTO.NotificationDTO;
import com.goaleaf.entities.DTO.pagination.NotificationPageDTO;
import com.goaleaf.entities.Notification;
import com.goaleaf.repositories.NotificationRepository;
import com.goaleaf.services.NotificationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.goaleaf.security.SecurityConstants.SECRET;

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
        return convertOneToDTO(notificationRepository.findByRecipientIDAndUrl(userID, url));
    }

    @Override
    public NotificationDTO findByDescription(String description) {
        return convertOneToDTO(notificationRepository.findByDescription(description));
    }

    @Override
    public Iterable<NotificationDTO> clearNtf(Integer userID) {
        Iterable<Notification> ntf = notificationRepository.getAllByRecipientID(userID);
        notificationRepository.delete(ntf);

        return getAllByUserID(userID);
    }

    @Override
    public NotificationPageDTO getUserNtfPaging(Integer pageNr, Integer objectsNr, String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token).getBody();

        Pageable pageable = new PageRequest(pageNr, objectsNr);
        Page<Notification> page = notificationRepository.findAllByRecipientIDOrderByDateDesc(Integer.parseInt(claims.getSubject()), pageable);
        Iterable<Notification> list = page.getContent();

        Iterable<NotificationDTO> output = convertManyToDTOs(list);

        return new NotificationPageDTO(output, page.getNumber(), page.hasPrevious(), page.hasNext(), page.getTotalPages());
    }

    private NotificationDTO convertOneToDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

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
