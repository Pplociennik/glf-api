package com.goaleaf.security;

import com.goaleaf.entities.Comment;
import com.goaleaf.entities.DTO.HabitDTO;
import com.goaleaf.entities.DTO.PostDTO;
import com.goaleaf.entities.Habit;
import com.goaleaf.entities.Notification;
import com.goaleaf.entities.Post;
import com.goaleaf.entities.enums.PostTypes;
import com.goaleaf.services.NotificationService;
import com.goaleaf.services.servicesImpl.BeanUtil;

import javax.mail.MessagingException;
import java.util.Date;

public class EmailNotificationsSender {

    private NotificationService notificationService = BeanUtil.getBean(NotificationService.class);

    private EmailSender sender;
    private String senderAddress;
    private String senderPassword;

    public EmailNotificationsSender() {
        sender = new EmailSender();
        senderAddress = "goaleaf@gmail.com";
        senderPassword = "spaghettiCode";
    }

    public Notification createInAppNotification(Integer recipientID, String description, String url, Boolean checkRepeats) {
        Notification ntf = new Notification();
        ntf.setDate(new Date());
        ntf.setRecipientID(recipientID);
        ntf.setDescription(description);
        ntf.setUrl(url);
        if (checkRepeats) {
            if (notificationService.findByDescription(ntf.getDescription()) == null) {
                notificationService.saveNotification(ntf);
            }
        } else {
            notificationService.saveNotification(ntf);
        }
        return ntf;
    }

    public void sendInvitationNotification(String recipientEmail, String recipientName, String inviterName, String habitTitle) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("You have a new invitation!");
        sender.setBody("Hello " + recipientName + "!\n\n" +
                "User " + inviterName + " invited you to joining a new contest \"" + habitTitle + "\" \n\n" +
                "If you want to take a challenge just log in to your GoaLeaf account: " + "http://www.goaleaf.com/login" + "\n\n" +
                "We wish you great results! :)\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void sayHello(String recipientEmail, String recipientName) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("GoaLeaf says hello! :)");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "We are excited to introduce you our challenge portal!" + "\n\n" +
                "Just log in, join our incredible community, work, support and just have fun! :)\n" +
                "And remember! You are the chosen one to change the look of your world!\n\n" +
                "We wish you great results! :)\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void challengeCreated(String recipientEmail, String recipientName, Habit newHabit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("Challenge " + newHabit.getHabitTitle() + " has been created!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "Your challenge \"" + newHabit.getHabitTitle() + "\" has been created!" + "\n\n" +
                "It is high time now to start inviting some members!\n" +
                "But! Don't forget to add some first tasks!\n\n" +
                "You don't want them to be lazy, aren't you? ;)\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void taskCreated(String recipientEmail, String recipientName, Habit newHabit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("New task has been added in challenge: " + newHabit.getHabitTitle() + "!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "There are new tasks available in challenge \"" + newHabit.getHabitTitle() + "\"\n\n" +
                "If you don't want to get messages like this just uncheck this option in you profile.\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void newMemberJoined(String recipientEmail, String recipientName, String userName, HabitDTO newHabit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("New member joined to your challenge!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "User " + userName + " joined to your challenge \"" + newHabit.getTitle() + "\"!\n\n" +
                "If you don't want to get messages like this just uncheck this option in you profile.\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void challengeEnded(String recipientEmail, String recipientName, String userName, Habit newHabit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("Challenge has ended!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "User " + userName + " won the challenge \"" + newHabit.getHabitTitle() + "\"!\n\n" +
                "You cannot complete tasks in this challenge anymore. However you are still able to write posts and comments.\n\n" +
                "If you don't want to get messages like this just uncheck this option in you profile.\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void postCommented(String recipientEmail, String recipientName, String userName, PostDTO post, Comment comment) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject(userName + " commented your post!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "User " + userName + " commented your post: \"" + (post.getPostType().equals(PostTypes.JustText) || post.getPostType().equals(PostTypes.Task) || post.getPostType().equals(PostTypes.HabitFinished) ? post.getPostText() : "<image>") + "\"!\n\n" +
                "Comment:\n\n" +
                "\"" + comment.getCommentText() + "\"" + "\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void postReacted(String recipientEmail, String recipientName, String userName, Post post) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject(userName + " reacted to your post!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "User " + userName + " reacted to your post: \"" + (post.getPostType().equals(PostTypes.JustText) || post.getPostType().equals(PostTypes.Task) || post.getPostType().equals(PostTypes.HabitFinished) ? post.getPostText() : "<image>") + "\"!\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void postAdded(String recipientEmail, String recipientName, String userName, HabitDTO habit, Post post) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject(userName + " added a new post!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "User " + userName + " added a new post in challenge \"" + habit.getTitle() + "\"!\n\n" +
                "Post:\n" +
                "\"" + (post.getPostType().equals(PostTypes.JustText) || post.getPostType().equals(PostTypes.Task) || post.getPostType().equals(PostTypes.HabitFinished) ? post.getPostText() : "<image>") + "\"" + "\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void challengeDeleted(String recipientEmail, String recipientName, Habit habit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("Challenge \"" + habit.getHabitTitle() + "\" has been deleted!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "Challenge \"" + habit.getHabitTitle() + "\" has been deleted!\n\n" +
                "It is no longer available to any user.\n " +
                "All the data like posts, reactions or comments has been also deleted permanently.\n" +
                "For more details, please, contact the challenge administrator.\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void goalUpdated(String recipientEmail, String recipientName, Habit habit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("The goal in \"" + habit.getHabitTitle() + "\" has been updated!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "Administrator of the challenge \"" + habit.getHabitTitle() + "\" has set a new goal!\n\n" +
                "It seems you've got more work to do :)\n " +
                "The challenge is available at:\n" +
                "http://www.goaleaf.com/challenge/" + habit.getId() + "\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }

    public void userBanned(String recipientEmail, String recipientName, Habit habit) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("You have been banned!");
        sender.setBody("Welcome " + recipientName + "!\n\n" +
                "Administrator of the challenge \"" + habit.getHabitTitle() + "\" has kicked you!\n\n" +
                "The challenge is no longer available to you.\n " +
                "For more details, please, contact the challenge administrator.\n\n" +
                "Your Sincerely\n" +
                "GoaLeaf group");
//        sender.addAttachment("TestFile.txt");
        sender.send();
    }
}
