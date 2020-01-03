package com.goaleaf.controllers;

import com.goaleaf.entities.DTO.NotificationDTO;
import com.goaleaf.services.NotificationService;
import com.goaleaf.services.UserService;
import com.goaleaf.validators.exceptions.accountsAndAuthorization.AccountNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/usersntf", method = RequestMethod.GET)
    public Iterable<NotificationDTO> getAllNotificationsByUserID(@RequestParam Integer userID) throws AccountNotExistsException {
        if (userService.findById(userID) == null)
            throw new AccountNotExistsException("Account does not exist!");

        return notificationService.getAllByUserID(userID);
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET)
    public Iterable<NotificationDTO> getAll() {
        return notificationService.getAll();
    }

    @RequestMapping(value = "/ntf/{id}", method = RequestMethod.DELETE)
    public void deleteFromDatabase(@RequestParam Integer ntfID) {
        notificationService.removeFromDatabaseByID(ntfID);
    }
}
