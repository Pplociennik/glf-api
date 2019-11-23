package com.goaleaf.security;

import javax.mail.MessagingException;

public class EmailNotificationsSender {

    private EmailSender sender;
    private String senderAddress;
    private String senderPassword;

    public EmailNotificationsSender() {
        sender = new EmailSender();
        senderAddress = "goaleaf@gmail.com";
        senderPassword = "spaghettiCode";
    }

    public void sendInvitationNotification(String recipientEmail, String recipientName, String inviterName, String habitTitle) throws MessagingException {

        sender.setSender(senderAddress, senderPassword);
        sender.addRecipient(recipientEmail);
        sender.setSubject("You have a new notification!");
        sender.setBody("Hello " + recipientName + "!\n\n" +
                "User " + inviterName + " invited you to joining a new contest: " + habitTitle + " \n\n" +
                "If you want to take a challenge just log in to your GoaLeaf account: " + "*/login" + "\n\n" +
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
}
