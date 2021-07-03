package com.example.lazyworkout.model;

import java.util.Calendar;
import java.util.Date;

import com.example.lazyworkout.model.UserMessage;
import com.google.firebase.firestore.ServerTimestamp;

public class Message {

    private String message;
    private Date dateCreated;
    private UserMessage userMessageSender;
    private String urlImage;

    public Message() { }

    public Message(String message, UserMessage userMessageSender) {
        this.message = message;
        this.userMessageSender = userMessageSender;
        this.dateCreated = Calendar.getInstance().getTime();

    }

    public Message(String message, String urlImage, UserMessage userMessageSender) {
        this.message = message;
        this.urlImage = urlImage;
        this.userMessageSender = userMessageSender;
        this.dateCreated = Calendar.getInstance().getTime();
    }

    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public UserMessage getUserSender() { return userMessageSender; }
    public String getUrlImage() { return urlImage; }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(UserMessage userMessageSender) { this.userMessageSender = userMessageSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
}